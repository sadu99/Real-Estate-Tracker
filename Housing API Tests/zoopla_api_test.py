# limited to 100 calls/hr
# HTTP response code	Description of response
# 200 - OK	The requested method was successful and the response indicates the expected result (if any).
# 400 - Bad Request	The request that was made could be satisfied as the parameters provided were not sufficient to
#       produce a valid response.
# 401 - Unauthorized	The API key could not be recognised and the request is not authorized.
# 403 - Forbidden	The requested method is not available for the API key specified.
# 404 - Not Found	A method was requested that is not available in the API version specified.
# 405 - Method Not Allowed	The HTTP request that was made requested an API method that can not process the HTTP method
#       used.
# 500 - Internal Server Error	An error occurred on the server that could be not be recovered from nor reported.
#       In this case no output will be generated.

import json
import requests
apiKey = '3nxkbhf75bxffdmgdhjz4ae2'
area = 'Birmingham'
url = 'http://api.zoopla.co.uk/api/v1/property_listings.json?area=' + area + '&api_key=' + apiKey
r = requests.get(url)
print r.content
json_temp = json.loads(r.content)
print json_temp['country']