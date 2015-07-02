***************************
**Contactos (Descripcion)**
***************************
***************************


**Contactos (Ver lista de contactos)**
======================================
======================================

 * Precondicion: MensajerO tiene que estar ejecutándose y el Usuario ya tiene que estar logueado al sistema.
 * Postcondicion: Usuario autenticado ve su lista de contactos actualizada.
 * Actores: Usuario autenticado y Administrador.
 * Flujo principal:
  1. Usuario autenticado presiona la pestaña de contactos.
  #. Administrador consulta la lista de contactos de Usuario autenticado.
  #. Administrador muestra la vista de contactos actualizada.


**Contactos (Agregar)**
=======================
=======================

 * Precondicion: MensajerO tiene que estar ejecutándose y el Usuario ya tiene que estar logueado al sistema.
 * Postcondicion: Un nuevo usuario se ha añadido a la lista de contactos del Usuario autenticado.
 * Actores: Usuario autenticado y Administrador.
 * Flujo principal:
  1. Usuario autenticado presiona la pestaña de contactos.
  #. Usuario autenticado pulsa el botón de “Agregar contacto”.
  #. Usuario autenticado completa en el menú desplegado el nombre de usuario a agregar y envia la informacion.
  #. Administrador valida los datos ingresados por Usuario autenticado.
  #. Si el nombre de usuario existe en sistema, Administrador agrega ese usuario a la lista de contactos de Usuario autenticado.
  #. Administrador muestra la vista de contactos actualizada.
 * Flujo alternativo 1:
  5. Si el usuario no existe o, Administrador indica a Usuario autenticado que los datos ingresados son incorrectos.
  #. Continúa desde el punto 2.


**Contactos (Eliminar)**
========================
========================

 * Precondicion: MensajerO tiene que estar ejecutándose, el Usuario ya tiene que estar logueado al sistema y tiene que tener al menos un contacto previamente agregado a su lista de contactos.
 * Postcondicion: El usuario elegido se ha borrado de la lista de contactos del Usuario autenticado.
 * Actores: Usuario autenticado y Administrador.
 * Flujo principal:
  1. Usuario autenticado presiona la pestaña de contactos.
  #. Usuario autenticado presiona durante 3 segundos o más sobre el contacto que desea eliminar.
  #. Usuario autenticado elije la opción “Delete User” del menú desplegado y posteriormente elije la opción “Ok”.
  #. Administrador borra el usuario seleccionado de la base de datos.
  #. Administrador muestra la vista de contactos actualizada.
  

**Contactos (Ver perfil)**
==========================
==========================

 * Precondicion: MensajerO tiene que estar ejecutándose, el Usuario ya tiene que estar logueado al sistema y tiene que tener al menos un contacto previamente agregado a su lista de contactos.
 * Postcondicion: El Usuario autenticado visualiza los datos de perfil de un contacto de la lista de contactos del Usuario autenticado.
 * Actores: Usuario autenticado y Administrador
 * Flujo principal:
  1. Usuario autenticado presiona la pestaña de contactos.
  #. Usuario autenticado presiona durante 3 segundos o más sobre el contacto que desea consultar.
  #. Usuario autenticado elije la opción “View User Profile” del menú desplegado.
  #. Administrador busca la información de perfil correspondiente al usuario elegido dentro de la base de datos.
  #. Administrador muestra la vista de perfil de contacto actualizada.


**Contactos (Iniciar una conversacion)**
========================================
========================================

 * Precondicion: MensajerO tiene que estar ejecutándose, el Usuario ya tiene que estar logueado al sistema y tiene que tener al menos un contacto previamente agregado a su lista de contactos.
 * Postcondicion: Se ha creado una nueva conversación con el contacto elegido y MensajerO muestra la vista de dicha conversación.
 * Actores: Usuario autenticado y Administrador
 * Flujo principal:
  1. Usuario autenticado presiona la pestaña de contactos.
  #. Usuario autenticado presiona durante 3 segundos o más sobre el contacto con el que desea comunicarse.
  #. Usuario autenticado elije la opción “Start Conversation” del menú desplegado.
  #. Administrador crea una nueva conversación en la base de datos.
  #. Administrador muestra la vista de conversación con el usuario elegido.
 * Flujo alternativo 1:
  2. Usuario autenticado presiona sobre el contacto con el que desea comunicarse. 
  #. Continúa desde el punto 4.


.. toctree::
   :maxdepth: 2
   
   contactos