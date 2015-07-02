************************
**Perfil (Descripcion)**
************************
************************


**Perfil (Seleccion de Avatar)**
================================
================================

 * Precondicion: MensajerO tiene que estar ejecutándose y el Usuario ya tiene que estar logueado al sistema.
 * Postcondicion: Se actualiza la foto de perfil del Usuario autenticado.
 * Actores: Usuario autenticado y Administrador.
 * Flujo principal:
  1. Usuario autenticado presiona el botón sobre la imagen de perfil actual.
  #. Administrador busca todas las imágenes disponibles y las devuelve a Usuario autenticado.
  #. Usuario autenticado presiona sobre la imagen que desea mostrar.
  #. Administrador muestra la vista de perfil actualizada.


**Perfil (Seleccion de estado)**
================================
================================

 * Precondicion: MensajerO tiene que estar ejecutándose y el Usuario ya tiene que estar logueado al sistema.
 * Postcondicion: El estado del Usuario autenticado se ha actualizado según lo precisado por él.
 * Actores: Usuario autenticado y Administrador.
 * Flujo principal:
  1. Usuario autenticado presiona el botón estado.
  #. Usuario autenticado presiona sobre el estado que desea en el menú desplegado.
  #. Administrador muestra la vista de perfil actualizada.


**Perfil (Seleccion de un alias)**
==================================
==================================

 * Precondicion: MensajerO tiene que estar ejecutándose y el Usuario ya tiene que estar logueado al sistema.
 * Postcondicion: El alias del Usuario autenticado se ha actualizado según lo precisado por él.
 * Actores: Usuario autenticado y Administrador.
 * Flujo principal:
  1. Usuario autenticado completa la sección de “Nickname” y presiona el boton "Ok".
  #. Administrador muestra la vista de perfil actualizada.


**Perfil (Seleccion de un mensaje de estado)**
==============================================
==============================================

 * Precondicion: MensajerO tiene que estar ejecutándose y el Usuario ya tiene que estar logueado al sistema.
 * Postcondicion: El mensaje de estado del Usuario autenticado se ha actualizado según lo precisado por él.
 * Actores: Usuario autenticado y Administrador.
 * Flujo principal:
  1. Usuario autenticado completa la sección de “Message” y presiona el boton "Ok".
  #. Administrador muestra la vista de perfil actualizada.



**Perfil (Guardar)**
====================
====================

 * Precondicion: MensajerO tiene que estar ejecutándose y el Usuario ya tiene que estar logueado al sistema.
 * Postcondicion: Toda la informacion de perfil modificada se ha actualizado.
 * Actores: Usuario autenticado y Administrador.
 * Flujo principal:
  #. Usuario autenticado presiona el botón “Guardar”.
  #. Administrador guarda en la base de datos la información de perfil actualizada.
  #. Administrador cambia la vista a la previa a entrar a perfil


**Perfil (Diagrama)**
=====================================
=====================================

.. toctree::
   :maxdepth: 2
   
   perfil