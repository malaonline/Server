function refreshDistance(url,lat,lng) {
    new Ajax.Request(url, {
        method: 'post',
        parameters: {lat:lat,lng:lng},
        onSuccess: function(transport) {
            alert('成功!')
    }
    }); // end new Ajax.Request
}

