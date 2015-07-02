***********************
**Login (Descripcion)**
***********************
***********************

**Login**
=========
=========

 * Precondicion: MensajerO tiene que estar ejecutándose y en la pantalla de login.
 * Postcondicion: El Usuario se encuentra logueado en el sistema de MensajerO.
 * Actores: Usuario y Administrador.
 * Flujo principal:
  1. Usuario completa los datos de nombre de usuario, contraseña y verificación de contraseña, y selecciona la opción “Ingresar”.
  #. Administrador valida los datos ingresados por Usuario.
  #. Si los datos fueron correctos, Administrador muestra la pantalla principal.
 * Flujo alternativo 1:
  3. Si el nombre de usuario no existe o la contraseña ingresada es incorrecta, Administrador indica a Usuario que los datos ingresados son incorrectos.
  #. Continúa desde el punto 1.
  
.. toctree::
   :maxdepth: 2
   
   login