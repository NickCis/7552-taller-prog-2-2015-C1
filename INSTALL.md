# Servidor

## Dependencias
* CMake: `CMake >= 2.9`
* GCC: `gcc >= 4.7`, hay que tener soporte de C++11
* [Rocksdb](https://github.com/facebook/rocksdb/blob/master/INSTALL.md): `rocksdb >= 3.9`. 

### Rocksdb
Rocksdb debe estar compilado como `shared library` y debe estar estar instalado en el sistema.
Algunas distribuciones cuentan con el paquete:
* [Arch Linux](https://aur.archlinux.org/packages/rocksdb/)

Para instalarlo manualmente:
```
$ wget https://github.com/facebook/rocksdb/archive/rocksdb-3.9.1.zip
$ unzip rocksdb-3.9.1.zip
$ cd rocksdb-rocksdb-3.9.1
$ make shared_lib
$ sudo install -d /usr/include
$ sudo cp -r include/rocksdb /usr/include
$ sudo install -m755 -D librocksdb.so /usr/lib/librocksdb.so
$ sudo install -D -m644 LICENSE "/usr/share/licenses/$pkgname/LICENSE"
```
## Compilaci&oacute;n

Para compilar el servidor, basta con crear una subcarpeta `build` donde se compilará y utilizar la herramienta CMake.
```
$ mkdir build
$ cd build
$ cmake ../server
$ make
```

