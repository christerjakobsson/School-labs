import requests
from requests_oauthlib import OAuth1
import json


client_key = '123456'
client_secret = '65xyAzi1'
token_key = 'n4oTQ22l4FxsuQlYZlhCFwYrrSgrlPn1lhIx32uzwzAwn4oTQ22l4FxsuQlYZlhC1F'
token_secret = '0MNOqkQncNHuKTi6fQ8MuA'
oauth = OAuth1(client_key=client_key, client_secret=client_secret,
               resource_owner_key=token_key, resource_owner_secret=token_secret,
               signature_type = 'auth_header')

client = requests.session()

body = {'title':'Test dataset', 'description':'Test description','defined_type':'dataset'}
headers = {'content-type':'application/json'}

response = client.post('http://api.figshare.com/v1/my_data/articles', auth=oauth,
                        data=json.dumps(body), headers=headers)

results = json.loads(response.content)
print results
