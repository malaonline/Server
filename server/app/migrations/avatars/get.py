import os
import requests

urls = [
        'http://www.ipb.cc/uploads/allimg/c150225/1424U493410560-2WK3.jpg',
        'http://www.baiju.org/weibopic/1571873837.jpg',
        'http://www.ipb.cc/uploads/allimg/c150225/1424U493619260-3M327.jpg',
        'http://www.ipb.cc/uploads/allimg/c150225/1424U493551250-34Q57.jpg',
        'http://www.ipb.cc/uploads/allimg/c150225/1424U4933W3P-2L593.jpg',
        'http://www.toux8.com/uploads/allimg/111119/1_111119200143_12.jpg',
        'http://imgst-dl.meilishuo.net/pic/r/3c/62/87ac7ec995a3ee6ec1563aff5d68_310_310.jpeg',
]

path = os.path.abspath(os.path.dirname(__file__))
for i, url in enumerate(urls):
    name = 'img{0:d}.jpg'.format(i)
    r = requests.get(url)
    open(os.path.join(path, name), 'wb').write(r.content)
