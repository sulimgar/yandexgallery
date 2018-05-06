from flask import Flask
from flask import request
import vk
import re

app = Flask(__name__)

group_id = '-23300841'
version = '5.74'
album_id = 0
photos_per_chank = 20


@app.route('/', methods=['GET'])
def get_photos():
    get_size = bool(request.args.get('get_size'))
    group_id = '-' + str(request.args.get('group_id'))

    sess = vk.Session()
    vk_api = vk.API(sess)

    response = vk_api.photos.getAlbums(owner_id=group_id, v=version)
    if get_size:
        size = response['items'][album_id]['size']
        return str(size)

    last_photo_loaded = int(request.args.get('last_photo_loaded'))
    group_album = response['items'][album_id]['id']
    response = vk_api.photos.get(
        owner_id=group_id, album_id=group_album, v=version)

    result = str(response['count'])

    for item in response['items'][last_photo_loaded:last_photo_loaded + photos_per_chank]:
        urls = {}
        for key in item.keys():
            res = re.split('photo_', key)
            if len(res) == 2:
                urls[int(res[1])] = item[key]

        r = ""
        for key in sorted(urls.keys()):
            r += urls[key] + ','
        result += '!' + r

    return result


if __name__ == '__main__':
    app.run(debug=True)
