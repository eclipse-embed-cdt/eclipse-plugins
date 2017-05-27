# semver

The semantic versioning code is included as source code from:

- https://github.com/zafarkhaja/jsemver

To compare versions, use:

```java
import com.github.zafarkhaja.semver.Version;

Version v1 = Version.valueOf("1.0.0-rc.1+build.1");
Version v2 = Version.valueOf("1.3.7+build.2.b8f12d7");

int result = v1.compareTo(v2);  // < 0
boolean result = v1.equals(v2); // false

boolean result = v1.greaterThan(v2);           // false
boolean result = v1.greaterThanOrEqualTo(v2);  // false
boolean result = v1.lessThan(v2);              // true
boolean result = v1.lessThanOrEqualTo(v2);     // true
```
