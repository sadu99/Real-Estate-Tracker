from pyzillow.pyzillow import ZillowWrapper, GetDeepSearchResults

address = '2020 NW 61st St'
zipcode = 'Seattle, WA'

zillow_data = ZillowWrapper('X1-ZWz1fo21axmw3v_aui2x')
deep_search_response = zillow_data.get_deep_search_results(address, zipcode)
result = GetDeepSearchResults(deep_search_response)

for k, v in result.__dict__.iteritems():
    print k, '-', v

