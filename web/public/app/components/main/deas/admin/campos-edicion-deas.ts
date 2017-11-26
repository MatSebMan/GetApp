export var edicionDea = {
    types: {
        provincia: 'text',
        partido: 'text',
        localidad: 'text',
        calle: 'text',
        numero: 'number',
        zona_protegida: 'text',        
        telefono: 'number',
        referencia_interna: 'text',
        persona_contacto: 'text',
        activo: 'boolean',
        en_uso: 'boolean',
        geolocalizacion: 'geoloc'
    },
    text: {
        provincia: 'Provincia',
        partido: 'Partido',
        localidad: 'Localidad',
        calle: 'Calle',
        numero: 'Número',
        zona_protegida: 'Nombre Zona Protegida',
        telefono: 'Teléfono',
        referencia_interna: 'Referencia de ubicación',
        persona_contacto: 'Persona de Contacto',
        activo: 'Activo',
        en_uso: 'En Uso',
        geolocalizacion: 'Localización'
    },   
    geoloc: {
        geolocalizacion: ['calle', 'numero', 'provincia', 'localidad', 'partido']
    } 
}