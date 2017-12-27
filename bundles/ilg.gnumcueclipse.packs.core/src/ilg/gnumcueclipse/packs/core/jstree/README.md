# JS Tree nodes

Internal trees are better stored in a structure inspired by JavaScript 
objects, i.e. objects with properties or arrays of objects.

There are 2 main public classes, JsObject and JsArray, both derived
from JsNode.

There are several differences from full JSON objects:

* scalar properties can be only strings
* object nodes automatically keep references to their parents

