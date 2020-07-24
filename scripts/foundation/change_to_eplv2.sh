#!/bin/bash

find . -type f ! -name "change_to_eplv2.sh" ! -path "./.git/*" ! \( -name "*.exsd" -or -name "*.properties" -or -name "*.c" -or -name "*.h" \) \
  -exec sed -i -E \
    -e 's@Eclipse Public License v1.0@Eclipse Public License 2.0@g' \
    -e 's@Eclipse Distribution License v1.0@Eclipse Public License 2.0@g' \
    -e 's@^([[:blank:]]*[^[:blank:]])([[:blank:]]*)(All rights reserved. )@\1\n\1\2@' \
    -e 's@\.[[:blank:]]*All rights reserved.@\.@' \
    -e 's@All rights reserved.([[:blank:]]*)@@' \
    -e 's@\&lt;a href=\&quot;http://www.eclipse.org/legal/epl-v10.html\&quot;\&gt;@\&lt;a href=\&quot;http://www.eclipse.org/legal/epl-2.0/\&quot;\&gt;@' \
    -e 's@at http://www.eclipse.org/legal/epl-v10.html@at https://www.eclipse.org/legal/epl-2.0/@' \
    -e 's@([[:blank:]]*[^[:blank:]]{1,2})([[:blank:]]*)http://www.eclipse.org/legal/epl-v10.html@\1\2https://www.eclipse.org/legal/epl-2.0/\n\1\n\1\2SPDX-License-Identifier: EPL-2.0@' \
    -e 's@([[:blank:]]*)http://www.eclipse.org/org/documents/edl-v10.php@\1https://www.eclipse.org/legal/epl-2.0/\n\1\n\1SPDX-License-Identifier: EPL-2.0@' \
    {} +

find . -type f ! -name "change_to_eplv2.sh" ! -path "./.git/*" \( -name "*.exsd" -or -name "*.properties" \) \
  -exec sed -i -E \
    -e 's@Eclipse Public License 1.0@Eclipse Public License 2.0@g' \
    -e 's@Eclipse Public License v1.0@Eclipse Public License 2.0@g' \
    -e 's@^([[:blank:]]*[^[:blank:]])([[:blank:]]*)(All rights reserved. )@\1\n\1\2@' \
    -e 's@\.[[:blank:]]*All rights reserved.@\.@' \
    -e 's@All rights reserved.([[:blank:]]*)@@' \
    -e 's@http://www.eclipse.org/legal/epl-v10.html@https://www.eclipse.org/legal/epl-2.0/@' \
    {} +

find . -type f ! -name "change_to_eplv2.sh" ! -path "./.git/*" \( -name "*.c" -or -name "*.h"  \) \
  -exec sed -i -E \
    -e 's@Eclipse Public License 1.0@Eclipse Public License 2.0@g' \
    -e 's@Eclipse Public License v1.0@Eclipse Public License 2.0@g' \
    -e 's@http://www.eclipse.org/legal/epl-v10.html@https://www.eclipse.org/legal/epl-2.0/@' \
    {} +

find . -type f ! -name "change_to_eplv2.sh" ! -name debug.product ! -path "./.git/*" ! -name "*.properties"  ! -name "template.xml" \
  -exec sed -i -E \
    -e 's@([[:blank:]]*)http://www.eclipse.org/legal/epl-v10.html@\1https://www.eclipse.org/legal/epl-2.0/\n\1\n\1SPDX-License-Identifier: EPL-2.0@' \
    {} +

find . -type f ! -name "change_to_eplv2.sh" ! -path "./.git/*" -name "build.properties" \
  -exec sed -i -E \
    -e 's@epl-v10.html@epl-v20.html@' \
    {} +

mkdir -p /tmp/eplv2
curl -s https://www.eclipse.org/legal/epl/epl-2.0/about.html > /tmp/eplv2/about.html
curl -s https://www.eclipse.org/org/documents/epl-2.0/EPL-2.0.txt > /tmp/eplv2/EPL-2.0.txt
curl -s http://www.eclipse.org/legal/epl/notice.html > /tmp/eplv2/notice.html
curl -s http://www.eclipse.org/legal/epl/notice.html > /tmp/eplv2/license.html
curl -s http://www.eclipse.org/legal/epl/feature.properties.txt > /tmp/eplv2/feature.properties.txt

find . -name about.html   -exec cp /tmp/eplv2/about.html {} \;
find . -name license.html   -exec cp /tmp/eplv2/license.html {} \;
find . -name notice.html   -exec cp /tmp/eplv2/notice.html {} \;
for i in `find . -name epl-v10.html`; do
  rm $i
  cp /tmp/eplv2/license.html `dirname $i`/epl-v20.html
done

for i in `find . -name feature.properties`; do
  sed -n '/URL of the "Feature License"/q;p' $i > /tmp/feature.properties
  mv /tmp/feature.properties $i
  cat /tmp/eplv2/feature.properties.txt >> $i
done

cp /tmp/eplv2/EPL-2.0.txt LICENSE
