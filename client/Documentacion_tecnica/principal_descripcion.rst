***************************
**Principal (Descripcion)**
***************************
***************************

**Principal (Cambio de vista)**
===============================
===============================

 * Precondicion: MensajerO tiene que estar ejecutándose y el Usuario ya tiene que estar logueado al sistema.
 * Postcondicion: El Usuario autenticado cambia a la vista elegida.
 * Actores: Usuario autenticado y Administrador.
 * Flujo principal:
  1. Usuario autenticado presiona la pestaña de contactos.
  #. Administrador cambia a vista de contactos.
 * Flujo alternativo 1:
  1. Usuario autenticado presiona la pestaña de conversaciones activas.
  #. Administrador cambia a vista de conversaciones activas.
 * Flujo alternativo 2:
  1. Usuario autenticado presiona la “Tecla Multifuncion Izquierda”.
  #. Usuario autenticado selecciona la opción “Profile” del menú desplegado.
  #. Administrador busca la información de perfil de Usuario autenticado en la base de datos.
  #. Administrador cambia a vista de perfil actualizada.
  
**Principal (Checkin)**
=======================
=======================

 * Precondicion: MensajerO tiene que estar ejecutándose y el Usuario ya tiene que estar logueado al sistema.
 * Postcondicion: Una nueva ubicación se ha registrado para Usuario autenticado.
 * Actores: Usuario autenticado y Administrador.
 * Flujo principal:
  1. Usuario autenticado presiona la “Tecla Multifuncion Izquierda”.
  #. Usuario autenticado selecciona la opción “Checkin” del menú desplegado.
  #. Usuario autenticado ingresa los datos de checkin en el menú desplegado y envia la informacion.
  #. Administrador envia la informacion de checkin al servidor.
  
**Principal (Mensaje Difusion)**
================================
================================

 * Precondicion: MensajerO tiene que estar ejecutándose y el Usuario ya tiene que estar logueado al sistema.
 * Postcondicion: Un mensaje de difusión se ha enviado a toda la lista de contactos del Usuario autenticado.
 * Actores: Usuario autenticado y Administrador.
 * Flujo principal:
  1. Usuario autenticado presiona la “Tecla Multifuncion Izquierda”.
  #. Usuario autenticado selecciona la opción “Nueva difusion” del menú desplegado.
  #. Usuario autenticado ingresa su mensaje en el menú desplegado y envia la informacion.
  #. Administrador consulta la lista de contactos de Usuario autenticado.
  #. Administrador envia el mensaje de difusion a toda la lista de contactos de Usuario autenticado.


**Principal (Recibir Mensaje)**
===============================
===============================

 * Precondicion: MensajerO tiene que estar ejecutándose y el Usuario ya tiene que estar logueado al sistema.
 * Postcondicion: Usuario autenticado recibio un mensaje.
 * Actores: Usuario autenticado y Administrador.
 * Flujo principal:
  1. Administrador recibe del servidor un mensaje nuevo de un usuario de la lista de contactos del Usuario autenticado.
  #. Si no existe conversacion activa con ese contacto, Administrador crea una conversacion activa nueva.
  #. Administrador crea un nuevo mensaje del contacto que lo envio.
  #. Administrador notifica a Usuario autenticado la llegada del nuevo mensaje.


**Principal (Diagrama)**
========================
========================

.. toctree::
   :maxdepth: 2
   
   principal