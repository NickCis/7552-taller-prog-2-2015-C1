**************************
**Registro (Descripcion)**
**************************
**************************

**Registro**
============
============

 * Precondicion: MensajerO tiene que estar ejecutándose y en la pantalla de login.
 * Postcondicion: El Usuario se encuentra registrado en el sistema de MensajerO.
 * Actores: Usuario y Administrador.
 * Flujo principal:
  1. Usuario selecciona la opción “Presione aquí si no tiene una cuenta MensajerO”
  #. Administrador muestra la pantalla de Registro.
  #. Usuario completa los datos de nombre de usuario, contraseña y verificación de contraseña, y selecciona la opción “Registrarse”.
  #. Administrador valida los datos ingresados por Usuario con el servidor.
  #. Si los datos fueron correctos, Administrador guarda los datos de Usuario en el servidor.
  #. Administrador indica a Usuario que el registro fue exitoso.
 * Flujo alternativo 1:
  5.  Si el usuario ya existe, Administrador indica a Usuario que el nombre de usuario elegido ya existe y que debe ingresar uno distinto.
  #. Continúa desde el punto 3.
 * Flujo alternativo 2:
  5. Si la contraseña no coincide con su verificación, Administrador indica a Usuario que la contraseña no coincidió y que debe volver a ingresarla.
  #. Continúa desde el punto 3.
