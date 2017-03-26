package main

import (
	//"bytes"
	//"fmt"
	//"net/http"
	//
	//"github.com/gin-gonic/gin"
	//"gopkg.in/zabawaba99/firego.v1"
	"log"
	"net/http"
	"github.com/gin-gonic/gin"
	//"bytes"
	"io/ioutil"
	"encoding/json"
	"fmt"
)

const (
	ZooplaApiKey = "3nxkbhf75bxffdmgdhjz4ae2"
	ZooplaBaseUrl = "http://api.zoopla.co.uk/api/v1/"
	ZooplaPropertyListingsUrl = "property_listings.json?"
)


func main() {

	// Initialize Firebase
	//f := firego.New("https://real-estate-tracker-49711.firebaseio.com/", nil)

	//a := map[string]interface{}{"foo":"bar", "oldfoo":{"yo":"yo"}}
	//
	//if err := f.Set(a); err != nil {
	//	log.Fatal(err)
	//}


	//mom := map[string]interface{}{
	//	"foo": "bar",
	//	"bar": 1,
	//	"bez": []string{"hello", "world"},}
	//if err := f.Set(mom); err != nil {
	//	log.Fatal(err)
	//}




	type Person struct {
		Id         int
		First_Name string
		Last_Name  string
	}

	router := gin.Default()

	// GET a person detail
	router.GET("/", func(c *gin.Context) {
		c.JSON(http.StatusOK, gin.H{
			"message": "hello",
		})
	})


	// Get all properties in an area
	router.GET("/location/:area", func(c *gin.Context) {

		area := c.Param("area")

		resp, err := http.Get(ZooplaBaseUrl + ZooplaPropertyListingsUrl + "listing_status=sale" + "&area=" +
			area + "&api_key=" + ZooplaApiKey)

		if err != nil {
			log.Fatal(err)
		}
		defer resp.Body.Close()
		body, err := ioutil.ReadAll(resp.Body)

		var response interface{}
		json.Unmarshal(body, &response)

		type Listing struct {
			Listing_id string
			Num_bathrooms string
			Num_bedrooms string
			Image_url string
			Latitude float64
			Longitude float64
			Property_type string
			Price_change interface{}
			Agent_name string
			Agent_address string
			Agent_phone string
			Displayable_address string
			Price interface{}
			Description string
			Category string
		}

		dataMap := response.(map[string]interface{})

		var result map[string]interface{}

		if error_string, ok := dataMap["error_string"]; ok {
			result = gin.H{
				"error": error_string,
				"count" : 0,
			}
		} else {

			listingsDirty := dataMap["listing"].([]interface{})

			listingsClean := make([]Listing, len(listingsDirty))

			for idx := range listingsDirty {

				item := listingsDirty[idx].(map[string]interface{})

				var listing Listing

				listing.Listing_id = item["listing_id"].(string)
				listing.Num_bathrooms = item["num_bathrooms"].(string)
				listing.Num_bedrooms = item["num_bedrooms"].(string)
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
				//listing.Short_description = item["short_description"].(string)
				listing.Description = item["description"].(string)
				listing.Category = item["category"].(string)

				listingsClean[idx] = listing

			}

			fmt.Println(listingsClean)

			result = gin.H{
				"listings": listingsClean,
				"count":    len(listingsClean),
			}
		}

		c.JSON(http.StatusOK, result)
	})

	//// POST new person details
	//router.POST("/person", func(c *gin.Context) {
	//	var buffer bytes.Buffer
	//	first_name := c.PostForm("first_name")
	//	last_name := c.PostForm("last_name")
	//	stmt, err := db.Prepare("insert into person (first_name, last_name) values(?,?);")
	//	if err != nil {
	//		fmt.Print(err.Error())
	//	}
	//	_, err = stmt.Exec(first_name, last_name)
	//
	//	if err != nil {
	//		fmt.Print(err.Error())
	//	}
	//
	//	// Fastest way to append strings
	//	buffer.WriteString(first_name)
	//	buffer.WriteString(" ")
	//	buffer.WriteString(last_name)
	//	defer stmt.Close()
	//	name := buffer.String()
	//	c.JSON(http.StatusOK, gin.H{
	//		"message": fmt.Sprintf(" %s successfully created", name),
	//	})
	//})
	//
	//// PUT - update a person details
	//router.PUT("/person", func(c *gin.Context) {
	//	var buffer bytes.Buffer
	//	id := c.Query("id")
	//	first_name := c.PostForm("first_name")
	//	last_name := c.PostForm("last_name")
	//	stmt, err := db.Prepare("update person set first_name= ?, last_name= ? where id= ?;")
	//	if err != nil {
	//		fmt.Print(err.Error())
	//	}
	//	_, err = stmt.Exec(first_name, last_name, id)
	//	if err != nil {
	//		fmt.Print(err.Error())
	//	}
	//
	//	// Fastest way to append strings
	//	buffer.WriteString(first_name)
	//	buffer.WriteString(" ")
	//	buffer.WriteString(last_name)
	//	defer stmt.Close()
	//	name := buffer.String()
	//	c.JSON(http.StatusOK, gin.H{
	//		"message": fmt.Sprintf("Successfully updated to %s", name),
	//	})
	//})
	//
	//// Delete resources
	//router.DELETE("/person", func(c *gin.Context) {
	//	id := c.Query("id")
	//	stmt, err := db.Prepare("delete from person where id= ?;")
	//	if err != nil {
	//		fmt.Print(err.Error())
	//	}
	//	_, err = stmt.Exec(id)
	//	if err != nil {
	//		fmt.Print(err.Error())
	//	}
	//	c.JSON(http.StatusOK, gin.H{
	//		"message": fmt.Sprintf("Successfully deleted user: %s", id),
	//	})
	//})
	router.Run(":3000")
}