package main

import (
	"gopkg.in/zabawaba99/firego.v1"
	"log"
	"net/http"
	"strconv"
	"github.com/gin-gonic/gin"
	"io/ioutil"
	"encoding/json"
	"fmt"
)

const (
	ZooplaApiKey = "3nxkbhf75bxffdmgdhjz4ae2"
	ZooplaBaseUrl = "http://api.zoopla.co.uk/api/v1/"
	ZooplaPropertyListingsUrl = "property_listings.json?"
)

type Listing struct {
	Listing_id string
	Num_bathrooms string
	Num_bedrooms string
	Image_url string
	Thumbnail_image_url string
	Latitude float64
	Longitude float64
	Property_type string
	Price_change interface{}
	Agent_name string
	Agent_address string
	Agent_phone string
	Displayable_address string
	Price interface{}
	Short_description string
	Category string
}

func getCleanListingsData(listingsDirty []interface{}) []Listing {
	listingsClean := make([]Listing, len(listingsDirty))
	for idx := range listingsDirty {
		var listing Listing
		item := listingsDirty[idx].(map[string]interface{})
		listing.Listing_id = item["listing_id"].(string)
		listing.Num_bathrooms = item["num_bathrooms"].(string)
		listing.Num_bedrooms = item["num_bedrooms"].(string)
		listing.Thumbnail_image_url = item["thumbnail_url"].(string)
		listing.Image_url = item["image_url"].(string)
		listing.Latitude = item["latitude"].(float64)
		listing.Longitude = item["longitude"].(float64)
		listing.Property_type = item["property_type"].(string)
		listing.Price_change = item["price_change"]
		listing.Agent_name = item["agent_name"].(string)
		listing.Agent_address = item["agent_address"].(string)
		listing.Agent_phone = item["agent_phone"].(string)
		listing.Displayable_address = item["displayable_address"].(string)
		listing.Price = item["price"]
		listing.Short_description = item["short_description"].(string)
		listing.Category = item["category"].(string)
		listingsClean[idx] = listing
	}
	return listingsClean
}


func main() {

	// Initialize Firebase
	f := firego.New("https://real-estate-tracker-49711.firebaseio.com/", nil)

	// Initialize REST Router
	router := gin.Default()

	// GET - server health
	router.GET("/", func(c *gin.Context) {
		c.JSON(http.StatusOK, gin.H{
			"message": "server is working...",
		})
	})

	// GET - all properties in an area
	router.GET("/location/:area", func(c *gin.Context) {

		area := c.Param("area")

		params := c.Request.URL.Query()

		paramsUrl := ""

		if len(params) > 0 {
			for k, v := range params {
				paramsUrl += "&" + k + "=" + v[0]
			}
		}

		resp, err := http.Get(ZooplaBaseUrl + ZooplaPropertyListingsUrl + "listing_status=sale" + "&area=" +
			area + "&api_key=" + ZooplaApiKey + "&page_size=30" + "&minimum_price=1" + paramsUrl)

		if err != nil {
			log.Fatal(err)
		}
		defer resp.Body.Close()
		body, err := ioutil.ReadAll(resp.Body)

		var response interface{}
		json.Unmarshal(body, &response)

		dataMap := response.(map[string]interface{})

		var result map[string]interface{}

		if error_string, ok := dataMap["error_string"]; ok {
			result = gin.H{
				"error": error_string,
				"count" : 0,
			}
		} else {

			listingsDirty := dataMap["listing"].([]interface{})
			listingsClean := getCleanListingsData(listingsDirty)

			result = gin.H{
				"listings": listingsClean,
				"count":    len(listingsClean),
			}
		}

		c.JSON(http.StatusOK, result)
	})

	// GET - all favourites for a user
	router.GET("/favourites", func(c *gin.Context) {

		userID := c.Query("userID")

		favouritesRef, err := f.Ref("users/" + userID + "/favourites")
		if err != nil {
			log.Fatal(err)
		}

		var favourites map[string]bool
		if err := favouritesRef.Value(&favourites); err != nil {
			log.Fatal(err)
		}

		listingsClean := make([]Listing, len(favourites))

		idx := 0

		for listingID := range favourites {

			resp, err := http.Get(ZooplaBaseUrl + ZooplaPropertyListingsUrl + "listing_id=" + listingID +
				"&api_key=" + ZooplaApiKey)

			if err != nil {
				log.Fatal(err)
			}
			defer resp.Body.Close()
			body, err := ioutil.ReadAll(resp.Body)

			var response interface{}
			json.Unmarshal(body, &response)

			dataMap := response.(map[string]interface{})

			listingsClean[idx] = getCleanListingsData(dataMap["listing"].([]interface{}))[0]

			idx++

		}

		var result map[string]interface{}

		result = gin.H{
			"listings": listingsClean,
			"count":    len(listingsClean),
		}

		c.JSON(http.StatusOK, result)
	})

	// POST - new user details
	router.POST("/user", func(c *gin.Context) {

		userID := c.PostForm("userID")
		name := c.PostForm("name")
		email := c.PostForm("email")

		usersRef, err := f.Ref("users/" + userID)
		if err != nil {
			log.Fatal(err)
		}
		v := map[string]interface{}{
			"name": name,
			"email": email,
		}

		if err := usersRef.Set(v); err != nil {
			log.Fatal(err)
		}

		c.JSON(http.StatusOK, gin.H{
			"message": fmt.Sprintf("successfully created user: %s", name),
		})
	})

	// PUT - update favourites for a user
	router.PUT("/user", func(c *gin.Context) {

		userID := c.PostForm("userID")
		listingID := c.PostForm("listingID")

		var removeListing bool
		removeListingValue, removeListingKey := c.GetPostForm("removeListing")

		if removeListingKey {
			val, _ := strconv.ParseBool(removeListingValue)
			removeListing = val
		} else {
			removeListing = false
		}

		favouritesRef, err := f.Ref("users/" + userID + "/favourites")
		if err != nil {
			log.Fatal(err)
		}

		var favourites map[string]bool
		if err := favouritesRef.Value(&favourites); err != nil {
			log.Fatal(err)
		}

		if favourites == nil {
			favourites = make(map[string]bool)
		}

		if removeListing {
			delete(favourites, listingID)
		} else {
			favourites[listingID] = true
		}

		if err := favouritesRef.Set(favourites); err != nil {
			log.Fatal(err)
		}

		c.JSON(http.StatusOK, gin.H{
			"message": fmt.Sprintf("successfully updated favourites for userID: %s", userID),
		})
	})

	router.Run(":3000")
}