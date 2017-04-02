package main

import (
	"log"
	"net/http"
	"io/ioutil"
	"encoding/json"
	"fmt"
	"math"
	"github.com/soniakeys/meeus/interp"
	"strconv"
)

const (
	ZooplaApiKey = "3nxkbhf75bxffdmgdhjz4ae2"
	ZooplaBaseUrl = "http://api.zoopla.co.uk/api/v1/"
	ZooplaPropertyListingsUrl = "property_listings.json?"
	ZooplaEstimatesUrl = "zoopla_estimates.json?"
)

func getPropertyXValues(propertyDirty []interface{},current_latitude(float64),current_longitude(float64)) []float64 {
	denominator := []float64{0,0,0,0,0}
	percentageChange := []float64{0,0,0,0,0}
	total_percentageChange := []float64{0,0,0,0,0}
	x_values := []float64{0,0,0,0,0}

	for idx := range propertyDirty {
		item := propertyDirty[idx].(map[string]interface{})
		property_latitude:= StringToFloat(item["latitude"].(string),current_latitude)
		property_longitude:= StringToFloat(item["longitude"].(string),current_longitude)
		percentageChange[0] = returnValue(item["percent_change_1year"])
		percentageChange[1] = returnValue(item["percent_change_2year"])
		percentageChange[2] = returnValue(item["percent_change_3year"])
		percentageChange[3] = returnValue(item["percent_change_4year"])
		percentageChange[4] = returnValue(item["percent_change_5year"])
		euclidean_distance := math.Sqrt(math.Pow((property_longitude - current_longitude),2) + math.Pow((property_latitude - current_latitude),2))

		for idx2:= range percentageChange{
			lastelement:=percentageChange[idx2]
			if lastelement != 0 && euclidean_distance!= 0{
				lastelement = lastelement/euclidean_distance
				total_percentageChange[idx2] = total_percentageChange[idx2] + lastelement
				denominator[idx2] = denominator[idx2] + 100/euclidean_distance
			}
		}
	}

	for idx:= range x_values {
		x_values[idx] = total_percentageChange[idx]/denominator[idx]
	}

	return x_values
}

func returnValue(x interface{}) float64 {
	switch x.(type) {
	case string:
		return StringToFloat(x.(string),0)
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

func StringToFloat(input_num string, default_num float64) float64 {
	// to convert a float number to a string
	i, _ := strconv.ParseFloat(input_num, 64)
	return i
}


func main(){
	current_latitude := 52.460583
	current_longitude := -1.930792

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
	propertyXValues := getPropertyXValues(propertyDirty,current_latitude,current_longitude)


	table := []struct{ X, Y float64 }{
		{1, propertyXValues[0]},
		{2, propertyXValues[1]},
		{3, propertyXValues[2]},
		{4, propertyXValues[3]},
		{5, propertyXValues[4]},
	}
	// 10 significant digits in input, no more than 10 expected in output
	fmt.Printf("10: %.10f\n", interp.Lagrange(1, table))
	fmt.Printf("15:  %.10f\n", interp.Lagrange(5, table))
	fmt.Printf("20: %.10f\n", interp.Lagrange(3.5, table))

}
