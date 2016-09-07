infos = {}

infos['dev'] = '''  <key>ApplicationProperties</key>
  <dict>
    <key>ApplicationPath</key>
    <string>Applications/parent-dev.app</string>
    <key>CFBundleIdentifier</key>
    <string>com.malalaoshi.dev</string>
    <key>CFBundleShortVersionString</key>
    <string>1.0</string>
    <key>CFBundleVersion</key>
    <string>1457</string>
    <key>SigningIdentity</key>
    <string>iPhone Developer</string>
  </dict>
'''

infos['stage'] = '''  <key>ApplicationProperties</key>
  <dict>
    <key>ApplicationPath</key>
    <string>Applications/parent-stage.app</string>
    <key>CFBundleIdentifier</key>
    <string>com.malalaoshi.app.stage</string>
    <key>CFBundleShortVersionString</key>
    <string>1.0</string>
    <key>CFBundleVersion</key>
    <string>1457</string>
    <key>SigningIdentity</key>
    <string>iPhone Developer</string>
  </dict>
'''

infos['prd'] = '''  <key>ApplicationProperties</key>
  <dict>
    <key>ApplicationPath</key>
    <string>Applications/parent-prd.app</string>
    <key>CFBundleIdentifier</key>
    <string>com.malalaoshi.app</string>
    <key>CFBundleShortVersionString</key>
    <string>1.0</string>
    <key>CFBundleVersion</key>
    <string>1457</string>
    <key>SigningIdentity</key>
    <string>iPhone Developer</string>
  </dict>
'''


with open('./build/parent.xcarchive/Info.plist') as f:
    lines = f.readlines()

for cnf in ('dev', 'stage', 'prd'):
    inserted = False
    with open('./build/%s-Info.plist' % cnf, 'w') as f:
        for line in lines:
            f.write(line)
            if not inserted and line.strip() == '<dict>':
                f.write(infos[cnf])
                inserted = True
