export var creacionDea = {
    types: {
        provincia: 'text',
        partido: 'text',
        localidad: 'text',
        calle: 'text',
        numero: 'number',
        zona_protegida: 'text',        
        telefono: 'number',
        horario: 'horario',
        referencia_interna: 'text',
        persona_contacto: 'text',
        geolocalizacion: 'geoloc'
    },
    text: {
        provincia: 'Provincia',
        partido: 'Partido',
        localidad: 'Localidad',
        calle: 'Calle',
        numero: 'Número',
        // latitud: 'Latitud',
        // longitud: 'Longitud',
        zona_protegida: 'Nombre Zona Protegida',
        telefono: 'Teléfono',
        horario: 'Horario',
        referencia_interna: 'Referencia de ubicación',
        persona_contacto: 'Persona de Contacto',
        geolocalizacion: 'Localización'
    },
    geoloc: {
        geolocalizacion: ['calle', 'numero', 'provincia', 'localidad', 'partido']
    }
}