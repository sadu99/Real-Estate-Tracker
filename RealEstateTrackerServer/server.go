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
	"math"
	"github.com/soniakeys/meeus/interp"
	"github.com/bradfitz/slice"
)

const (
	ZooplaApiKey = "3nxkbhf75bxffdmgdhjz4ae2"
	ZooplaBaseUrl = "http://api.zoopla.co.uk/api/v1/"
	ZooplaPropertyListingsUrl = "property_listings.json?"
	ZooplaEstimatesUrl = "zoopla_estimates.json?"
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
	Expected_value float64
}

func getCleanListingsData(listingsDirty []interface{}, projected_years(string)) []Listing {
	listingsClean := []Listing{}
	for idx := range listingsDirty {
		item := listingsDirty[idx].(map[string]interface{})
		if returnValue(item["price"]) != 0 {
			var listing Listing
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
			listing.Price = returnValue(item["price"])
			listing.Short_description = item["short_description"].(string)
			listing.Category = item["category"].(string)
			listing.Expected_value = getPropertyXValues(listing.Latitude,listing.Longitude,projected_years)
			listingsClean = append(listingsClean, listing)
		}
	}
	if len(listingsClean) > 0 {
		slice.Sort(listingsClean[:], func(i, j int) bool {
			return listingsClean[i].Expected_value > listingsClean[j].Expected_value
		})
	}
	return listingsClean
}

func getPropertyXValues(current_latitude(float64), current_longitude(float64), projected_years(string)) float64 {

	resp, err := http.Get(ZooplaBaseUrl + ZooplaEstimatesUrl + "latitude=" + FloatToString(current_latitude) +
		"&longitude=" + FloatToString(current_longitude) + "&radius=0.05&api_key=" + ZooplaApiKey)

	if err != nil {
		log.Fatal(err)
	}

	defer resp.Body.Close()
	body, err := ioutil.ReadAll(resp.Body)

	var response interface{}
	json.Unmarshal(body, &response)

	dataMap := response.(map[string]interface{})
	propertyDirty := dataMap["property"].([]interface{})

	denominator := []float64{0,0,0,0,0}
	percentageChange := []float64{0,0,0,0,0}
	total_percentageChange := []float64{0,0,0,0,0}
	x_values := []float64{0,0,0,0,0}

	for idx := range propertyDirty {
		item := propertyDirty[idx].(map[string]interface{})
		property_latitude:= StringToFloat(item["latitude"].(string))
		property_longitude:= StringToFloat(item["longitude"].(string))
		percentageChange[0] = returnValue(item["percent_change_1year"])
		percentageChange[1] = returnValue(item["percent_change_2year"])
		percentageChange[2] = returnValue(item["percent_change_3year"])
		percentageChange[3] = returnValue(item["percent_change_4year"])
		percentageChange[4] = returnValue(item["percent_change_5year"])
		euclidean_distance := math.Sqrt(math.Pow((property_longitude - current_longitude),2) + math.Pow((property_latitude - current_latitude),2))

		for idx2:= range percentageChange{
			lastelement:=percentageChange[idx2]
			if lastelement != 0 && euclidean_distance!= 0 {
				lastelement = lastelement/euclidean_distance
				total_percentageChange[idx2] = total_percentageChange[idx2] + lastelement
				denominator[idx2] = denominator[idx2] + 100/euclidean_distance
			}
		}
	}

	for idx:= range x_values {
		x_values[idx] = total_percentageChange[idx]/denominator[idx]
	}

	table := []struct{ X, Y float64 }{
		{1, x_values[0]},
		{2, x_values[1]},
		{3, x_values[2]},
		{4, x_values[3]},
		{5, x_values[4]},
	}

	float_projected_years := StringToFloat(projected_years)
	if float_projected_years < 5 {
		return interp.Lagrange(float_projected_years, table)
	} else {
		return interp.Lagrange(math.Mod(float_projected_years,5), table) +
			math.Floor(float_projected_years/5)*interp.Lagrange(5, table)
	}
}

func returnValue(x interface{}) float64 {
	switch x.(type) {
	case string:
		return StringToFloat(x.(string))
	case float32,float64:
		return x.(float64)
	case nil:
		return 0;
	default:
		return 0;
	}
}

func FloatToString(input_num float64) string {
	// to convert a float number to a string
	return strconv.FormatFloat(input_num, 'f', 6, 64)
}

func StringToFloat(input_num string) float64 {
	// to convert a float number to a string
	i, _ := strconv.ParseFloat(input_num, 64)
	return i
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
		projected_years := "5"

		if len(params) > 0 {
			for k, v := range params {
				if k == "projected_years" {
					projected_years = v[0]
				} else {
					paramsUrl += "&" + k + "=" + v[0]
				}
			}
		}

		resp, err := http.Get(ZooplaBaseUrl + ZooplaPropertyListingsUrl + "listing_status=sale" + "&area=" +
			area + "&api_key=" + ZooplaApiKey + "&page_size=18" + "&minimum_price=1" + paramsUrl)

		if err != nil {
			log.Fatal(err)
		}
		defer resp.Body.Close()
		body, err := ioutil.ReadAll(resp.Body)

		var response interface{}
		json.Unmarshal(body, &response)

		if string(body) == "<h1>Developer Over Rate</h1>" {
			c.JSON(http.StatusBadRequest, gin.H{
				"message": fmt.Sprintf("API Limit"),
			})
		} else {
			dataMap := response.(map[string]interface{})

			var result map[string]interface{}


			if error_string, ok := dataMap["error_string"]; ok {
				result = gin.H{
					"error": error_string,
					"count" : 0, }
			} else {

				listingsDirty := dataMap["listing"].([]interface{})
				listingsClean := getCleanListingsData(listingsDirty, projected_years)


				result = gin.H{
					"listings": listingsClean,
					"count":    len(listingsClean),
				}
			}
			c.JSON(http.StatusOK, result)
		}
	})

	// GET - all favourites for a user
	router.GET("/favourites", func(c *gin.Context) {

		userID := c.Query("userID")

		if len(userID) <= 0 {
			c.JSON(http.StatusBadRequest, gin.H{
				"message": fmt.Sprintf("userID is a required field."),
			})
		} else {
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

				listingsClean[idx] = getCleanListingsData(dataMap["listing"].([]interface{}),"5")[0]

				idx++

			}

			var result map[string]interface{}

			result = gin.H{
				"listings": listingsClean,
				"count":    len(listingsClean),
			}

			c.JSON(http.StatusOK, result)
		}
	})

	// POST - new user details
	router.POST("/user", func(c *gin.Context) {

		type User struct {
			UserID string `form:"userID" json:"userID"`
			Name string `form:"name" json:"name"`
			Email string `form:"email" json:"email"`
		}

		var user User
		if c.BindJSON(&user) == nil {
			if len(user.UserID) <= 0 {
				c.JSON(http.StatusBadRequest, gin.H{
					"message": fmt.Sprintf("userID is a required field."),
				})
			} else {
				usersRef, err := f.Ref("users/" + user.UserID)
				if err != nil {
					log.Fatal(err)
				}
				v := map[string]interface{}{
					"name": user.Name,
					"email": user.Email,
				}

				if err := usersRef.Update(v); err != nil {
					log.Fatal(err)
				}

				c.JSON(http.StatusOK, gin.H{
					"message": fmt.Sprintf("successfully created user: %s", user.Name),
				})
			}
		}
	})

	// PUT - update favourites for a user
	router.PUT("/user", func(c *gin.Context) {

		type FavouritesRequest struct {
			UserID string `form:"userID" json:"userID"`
			ListingID string `form:"listingID" json:"listingID"`
			RemoveListing string `form:"removeListing" json:"removeListing"`
		}

		var favReq FavouritesRequest

		if c.BindJSON(&favReq) == nil {
			if len(favReq.UserID) <= 0 || len(favReq.ListingID) <= 0 {
				c.JSON(http.StatusBadRequest, gin.H{
					"message": fmt.Sprintf("userID and listingID are required fields."),
				})
			} else {
				var removeListing bool

				if len(favReq.RemoveListing) <= 0 {
					removeListing = false
				} else {
					val, _ := strconv.ParseBool(favReq.RemoveListing)
					removeListing = val
				}

				favouritesRef, err := f.Ref("users/" + favReq.UserID + "/favourites")
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
					delete(favourites, favReq.ListingID)
				} else {
					favourites[favReq.ListingID] = true
				}

				if err := favouritesRef.Set(favourites); err != nil {
					log.Fatal(err)
				}

				c.JSON(http.StatusOK, gin.H{
					"message": fmt.Sprintf("successfully updated favourites for userID: %s", favReq.UserID),
				})
			}
		}
	})

	router.Run(":3000")
}