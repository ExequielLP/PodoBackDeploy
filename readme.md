## TO-DO:

- [ ] Limpiar south('').
- [ ] Manejo de errores amigables para los logs.

## - [ ] Crear querys para buscar por fecha inici-fin las tablas de turnos.
- [ ] Crear querys para buscar por servicio (nombre).
- [ ] Crear querys para buscar por usuario (nombre/apellido).

## TO-CHECK:

- [⚠️]
- [✔]


//Query día
- Turnos del dia [date] --> se usa en las cards y en la lista en el dashboard
- Turnos del día calendario de servicios usuario


## IMPORTANT:
//Query mes
- Para sacar ingresos del servicio al mes
- Para traer los turnos del mes (libres/ocupados)
- Para ver turnos en las talbas filtrados por mes y ocupados
- Para ver feriados del mes
{
    servicio: nombre , costo
}
//Esta se usa en la tabla de turnos del admin --> podría utilziarse para traer turnos del mes
http://localhost:8080/adminController/listaTurnoAdmin?fechaInicioMes=2024-10-1&fechaFinMes=2024-10-31
[
{
 "id": "f3db5043-c46a-4bbe-8095-cf5bbf2d60f8",
 "nombreServicio": "Servicio General",
 "costo": 12800.0,
 "startTime": "2024-10-23T09:00:00",
 "endTime": "2024-10-23T10:00:00",
 "estado": true,
 "nombreUsuario": "Administrador"
},
//En caso de q los turnos no esten reservados llegarian así
{
 "id": "f3db5043-c46a-4bbe-8095-cf5bbf2d60f8",
 "nombreServicio": "null",
 "costo": null,
 "startTime": "2024-10-23T09:00:00",
 "endTime": "2024-10-23T10:00:00",
 "estado": false,
 "nombreUsuario": "null"
},
]

//Se usa en el calendario de servicio
http://localhost:8080/Turnos/turnoDelDia?date=2024-10-25

// --> &fechaInicioMes=2024-10-1&fechaFinMes=2024-10-31
//TURNOS USUARIOS - TURNOS ADMIN
[
    {
        "id": "0f67f575-afe5-419f-aa5a-4d9d87b35ddc",
        "nombreServicio": null || "servicio",
        "costo": 0.0 || "costo",
        "startTime": "2024-10-25T14:00:00",
        "endTime": "2024-10-25T15:00:00",
        "estado": true,
        "nombreUsuario": null || "usuario"
    },
]