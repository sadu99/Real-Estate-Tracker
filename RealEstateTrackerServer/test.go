package main

import (
	"net/http"
	"log"
	"io/ioutil"
	"fmt"
	"encoding/json"
)

const (
	ZooplaApiKey = "3nxkbhf75bxffdmgdhjz4ae2"
	ZooplaBaseUrl = "http://api.zoopla.co.uk/api/v1/"

)

func main()  {

	apiKey := "3nxkbhf75bxffdmgdhjz4ae2"
	area := "london"
	url := "http://api.zoopla.co.uk/api/v1/property_listings.json?listing_status=sale&area=" + area + "&api_key=" + apiKey

	fmt.Println(url)

	resp, err := http.Get(url)
	if err != nil {
		log.Fatal(err)
	}
	defer resp.Body.Close()
	body, err := ioutil.ReadAll(resp.Body)

	var f interface{}

	type Listing struct {
		Agent_logo string
		Last_published_date string
		Num_recepts string
		Num_bathrooms string
		Image_url string
		Num_floors string
		Thumbnail_url string
		County string
		Image_caption string
		Price_change []interface{}
		Post_town string
		Price string
		Displayable_address string
		Street_name string
		Image_80_60_url string
		Status string
		Agent_name string
		Country string
		Num_bedrooms string
		Category string
		Property_type string
		Listing_id string
		Image_645_430_url string
		Listing_status string
		Longitude float32
		Description string
		Property_report_url string
		Price_modifier string
		Agent_phone string
		Country_code string
		Image_150_113_url string
		Agent_address string
		Short_description string
		Latitude float32
		Floor_plan []interface{}
		Image_354_255_url string
		Image_50_38_url string
		Details_url string
		Outcode string
		First_published_date string

	}

	type Result struct {
		Latitude float32
		County string
		Postcode string
		Country string
		Result_count int
		Area_name string
		Street string
		Town string
		Longitude float32
		Listing []Listing
		Bounding_box interface{}
	}



	var result Result

	json.Unmarshal(body, &result)

	json.Unmarshal(body, &f)

	fmt.Println(result)

	m := f.(map[string]interface{})
	for k, v := range m {
		fmt.Print(k)
		fmt.Print(": ")
		fmt.Println(v)
	}

	fmt.Println("\n\n\n")

	s := m["listing"].([]interface{})

	//fmt.Print(s)

	for x := range s {

		n := s[x].(map[string]interface{})

		var test_str string

		test_str = n["price"].(string)

		fmt.Println(test_str)

		for k, v := range n {
			fmt.Print(k)
			fmt.Print(": ")
			fmt.Println(v)
		}

		fmt.Println("\n\n")
	}




}

