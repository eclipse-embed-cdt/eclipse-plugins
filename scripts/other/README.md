
# DEPRECATED scripts

## Vectors

Always use the sed script to get the vectors from the assembly file:

```console
$ sed -f vectors.sed startup_stm32f411xe.s >x.s
```

# Copy from µOS++ (DEPRECATED)

```console
$ cd .../scripts
$ bash copy-from-micro-os.sh
```
