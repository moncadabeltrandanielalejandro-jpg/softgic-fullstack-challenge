Feature: Ciclo de vida de una solicitud operacional

  Background:
    * url baseUrl
    * def categoriaId = 'reemplazar-con-uuid-de-categoria-semilla'

  Scenario: A1 - Solicitante autenticado registra una solicitud válida
    Given path '/solicitudes'
    And header Authorization = 'Bearer ' + tokenSolicitante
    And request { asunto: 'Falla en impresora', descripcion: 'La impresora del piso 3 no enciende', categoriaId: '#(categoriaId)', prioridad: 'MEDIA' }
    When method post
    Then status 201
    And match response.estado == 'REGISTRADA'
    * def solicitudId = response.id

  Scenario: A3 - Usuario sin rol SUPERVISOR intenta cerrar y recibe 403
    Given path '/solicitudes'
    And header Authorization = 'Bearer ' + tokenSolicitante
    And request { asunto: 'Prueba A3', descripcion: 'Descripción de prueba', categoriaId: '#(categoriaId)', prioridad: 'BAJA' }
    When method post
    Then status 201
    * def solicitudId = response.id

    Given path '/solicitudes/' + solicitudId + '/transiciones/cerrar'
    And header Authorization = 'Bearer ' + tokenSolicitante
    And request { motivo: 'Intento no autorizado' }
    When method post
    Then status 403

  Scenario: Recorrido REGISTRADA -> EN_ATENCION -> RESUELTA
    Given path '/solicitudes'
    And header Authorization = 'Bearer ' + tokenSolicitante
    And request { asunto: 'Recorrido feliz', descripcion: 'Descripción del recorrido', categoriaId: '#(categoriaId)', prioridad: 'ALTA' }
    When method post
    Then status 201
    * def solicitudId = response.id

    Given path '/solicitudes/' + solicitudId + '/asignaciones'
    And header Authorization = 'Bearer ' + tokenAnalista
    When method post
    Then status 200

    Given path '/solicitudes/' + solicitudId
    And header Authorization = 'Bearer ' + tokenAnalista
    When method get
    Then status 200
    And match response.estado == 'EN_ATENCION'

    Given path '/solicitudes/' + solicitudId + '/transiciones/resolver'
    And header Authorization = 'Bearer ' + tokenAnalista
    And request { observacion: 'Se reemplazó el cartucho de tóner' }
    When method post
    Then status 200

    Given path '/solicitudes/' + solicitudId
    And header Authorization = 'Bearer ' + tokenAnalista
    When method get
    Then status 200
    And match response.estado == 'RESUELTA'
