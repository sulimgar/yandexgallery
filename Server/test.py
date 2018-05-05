import requests
r = requests.get(
    'http://neuro.pythonanywhere.com?last_photo_loaded=123&get_size=true')
print(r.text)
