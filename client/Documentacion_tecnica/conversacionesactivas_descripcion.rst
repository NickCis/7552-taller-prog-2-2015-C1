****************************************
**Conversaciones Activas (Descripcion)**
****************************************
****************************************


**Conversaciones Activas (Ver lista de conversaciones activas)**
================================================================
================================================================

 * Precondicion: MensajerO tiene que estar ejecutándose, el Usuario ya tiene que estar logueado al sistema y tiene que tener al menos una conversación activa con algún contacto.
 * Postcondicion: Usuario autenticado ve su lista de conversaciones activas actualizada.
 * Actores: Usuario autenticado y Administrador.
 * Flujo principal:
  1. Usuario autenticado presiona la pestaña de conversaciones activas.
  #. Administrador consulta la lista de conversaciones activas de Usuario autenticado.
  #. Administrador muestra la vista de conversaciones activas actualizada.


**Conversaciones Activas (Eliminar)**
=====================================
=====================================

 * Precondicion: MensajerO tiene que estar ejecutándose, el Usuario ya tiene que estar logueado al sistema y tiene que tener al menos una conversación activa con algún contacto.
 * Postcondicion: La conversación activa elegida fue borrada de la base de datos.
 * Actores: Usuario autenticado y Administrador.
 * Flujo principal:
  1. Usuario autenticado presiona la pestaña de conversaciones activas.
  #. Usuario autenticado presiona durante 3 segundos o más sobre la conversación activa que desea eliminar.
  #. Usuario autenticado selecciona la opción “Ok” del menú desplegado.
  #. Administrador borra la conversación activa de la base de datos.


**Conversaciones Activas (Seleccionar)**
========================================
========================================

 * Precondicion: MensajerO tiene que estar ejecutándose y el Usuario ya tiene que estar logueado al sistema.
 * Postcondicion: El Usuario autenticado visualiza la conversacion activa seleccionada y sus mensajes.
 * Actores: Usuario autenticado y Administrador.
 * Flujo principal:
  1. Usuario autenticado presiona la pestaña de conversaciones activas.
  #. Usuario autenticado presiona sobre una conversacion activa.
  #. Administrador busca los mensajes asociados a esa conversacion activa ordenados por fecha.
  #. Administrador actualiza la vista de conversacion con los mensajes.
  #. Administrador muestra la vista de conversacion actualizada.


**Conversaciones Activas (Diagrama)**
=====================================
=====================================

.. toctree::
   :maxdepth: 2
   
   conversacionesactivas