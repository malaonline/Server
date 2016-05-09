# !/usr/bin/sh
if [ "$1" = "-h" -o "$1" = "--help" ]; then
  echo "
-Dmala.host=dev.malalaoshi.com    # the host to test
-Dmala.user.count=2               # jmeter threads count
-Dmala.test.time=2                # within how many seconds to burst up these threads
-Dmala.loop.count=1               # 
-Dmala.preview.count=5            # preview date-time to attend courses
-t test.jmx
"
  exit
fi
jmeter -n -l "./sample.log" --loglevel "jmeter=WARN" -DCookieManager.save.cookies=true $@

