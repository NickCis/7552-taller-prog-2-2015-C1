********************************
**Conversaciones (Descripcion)**
********************************
********************************


**Conversaciones (Enviar mensaje)**
===================================
===================================

 * Precondicion: MensajerO tiene que estar ejecutándose, el Usuario ya tiene que estar logueado al sistema y tiene que tener al menos una conversación activa con algún contacto.
 * Postcondicion: Usuario autenticado ha enviado un mensaje a un contacto.
 * Actores: Usuario autenticado y Administrador.
 * Flujo principal:
  1. Usuario autenticado escribe un mensaje.
  #. Usuario autenticado presiona el boton de enviar mensaje.
  #. Administrador crea un mensaje nuevo con lo ingresado por Usuario autenticado en la base de datos.
  #. Administrador envia el mensaje al contacto.


**Conversaciones (Recibir mensaje)**
====================================
====================================

 * Precondicion: MensajerO tiene que estar ejecutándose, el Usuario ya tiene que estar logueado al sistema y tiene que tener al menos una conversación activa con algún contacto.
 * Postcondicion: Usuario autenticado ha recibido un mensaje de un contacto.
 * Actores: Usuario autenticado y Administrador.
 * Flujo principal:
  1. Usuario autenticado entra a la conversacion activa con el contacto.
  #. Administrador recibe del servidor un mensaje nuevo del contacto
  #. Administrador crea un nuevo mensaje del contacto que lo envio.
  #. Administrador notifica a Usuario autenticado la llegada del nuevo mensaje.
  #. Administrador actualiza la vista de conversacion del Usuario autenticado.


**Conversacion (Diagrama)**
===========================
===========================

.. toctree::
   :maxdepth: 2
   
   conversacion