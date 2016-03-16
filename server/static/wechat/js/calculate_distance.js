function getRad(d){   
    var PI = Math.PI;    
   return d*PI/180.0;    
}     
/** 
     * approx distance between two points on earth ellipsoid ,return the distance  in  kilometer.
     * @param {Object} lat1   
     * @param {Object} lng1   
     * @param {Object} lat2   
     * @param {Object} lng2   
     */     
    function CoolWPDistance(lat1,lng1,lat2,lng2){     
        var f = getRad((lat1 + lat2)/2);     
        var g = getRad((lat1 - lat2)/2);     
        var l = getRad((lng1 - lng2)/2);     
        var sg = Math.sin(g);     
        var sl = Math.sin(l);     
        var sf = Math.sin(f);     
        var s,c,w,r,d,h1,h2;     
        var a = 6378137.0;//The Radius of eath in meter.   
        var fl = 1/298.257;     
        sg = sg*sg;     
        sl = sl*sl;     
        sf = sf*sf;     
        s = sg*(1-sl) + (1-sf)*sl;     
        c = (1-sg)*(1-sl) + sf*sl;     
        w = Math.atan(Math.sqrt(s/c));     
        r = Math.sqrt(s*c)/w;     
        d = 2*w*a;     
        h1 = (3*r -1)/2/c;     
        h2 = (3*r +1)/2/s;     
        s = d*(1 + fl*(h1*sf*(1-sg) - h2*(1-sf)*sg));   
        s = s/1000;   
        s = s.toFixed(0);//指定小数点后的位数。   
        return s;     
    }    