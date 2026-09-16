package com.softgic.solicitudes.infrastructure.adapter.in.rest;

import com.softgic.solicitudes.domain.port.in.*;
import com.softgic.solicitudes.domain.port.in.RegistrarSolicitudUseCase.Comando;
import com.softgic.solicitudes.infrastructure.adapter.in.rest.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/solicitudes")
@Tag(name = "Solicitudes")
public class SolicitudController {

    private final RegistrarSolicitudUseCase registrarSolicitud;
    private final TomarSolicitudUseCase tomarSolicitud;
    private final ResolverSolicitudUseCase resolverSolicitud;
    private final GestionarSupervisionUseCase gestionarSupervision;
    private final SolicitudQueryService solicitudQueryService;

    public SolicitudController(RegistrarSolicitudUseCase registrarSolicitud, TomarSolicitudUseCase tomarSolicitud,
                                ResolverSolicitudUseCase resolverSolicitud,
                                GestionarSupervisionUseCase gestionarSupervision,
                                SolicitudQueryService solicitudQueryService) {
        this.registrarSolicitud = registrarSolicitud;
        this.tomarSolicitud = tomarSolicitud;
        this.resolverSolicitud = resolverSolicitud;
        this.gestionarSupervision = gestionarSupervision;
        this.solicitudQueryService = solicitudQueryService;
    }

    @PostMapping
    @PreAuthorize("hasRole('SOLICITANTE')")
    @Operation(summary = "Registrar una nueva solicitud (A1)")
    public ResponseEntity<SolicitudResponse> crear(@Valid @RequestBody CrearSolicitudRequest request,
                                                    Authentication authentication) {
        UUID solicitanteId = actorId(authentication);
        var solicitud = registrarSolicitud.ejecutar(new Comando(request.asunto(), request.descripcion(),
                request.categoriaId(), request.prioridad(), solicitanteId));
        var body = SolicitudResponse.from(solicitud);
        return ResponseEntity.created(URI.create("/api/v1/solicitudes/" + body.id())).body(body);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SOLICITANTE','ANALISTA','SUPERVISOR')")
    @Operation(summary = "Listar/filtrar solicitudes paginadas")
    public ResponseEntity<?> listar(@RequestParam(name = "estado", required = false) String estado,
                                     @RequestParam(name = "page", defaultValue = "0") int page,
                                     @RequestParam(name = "size", defaultValue = "20") int size,
                                     Authentication authentication) {
        return ResponseEntity.ok(solicitudQueryService.buscar(estado, page, size, authentication));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SOLICITANTE','ANALISTA','SUPERVISOR')")
    public ResponseEntity<SolicitudResponse> detalle(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(solicitudQueryService.obtenerDetalle(id));
    }

    @PostMapping("/{id}/asignaciones")
    @PreAuthorize("hasRole('ANALISTA')")
    @Operation(summary = "Tomar una solicitud (A2: solo un analista puede ganar la asignación)")
    public ResponseEntity<Void> tomar(@PathVariable("id") UUID id, Authentication authentication) {
        tomarSolicitud.ejecutar(id, actorId(authentication));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/transiciones/resolver")
    @PreAuthorize("hasRole('ANALISTA')")
    public ResponseEntity<Void> resolver(@PathVariable("id") UUID id, @Valid @RequestBody ResolverSolicitudRequest request,
                                          Authentication authentication) {
        resolverSolicitud.ejecutar(id, actorId(authentication), request.observacion());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/transiciones/devolver")
    @PreAuthorize("hasRole('SUPERVISOR')")
    public ResponseEntity<Void> devolver(@PathVariable("id") UUID id, @Valid @RequestBody MotivoRequest request,
                                          Authentication authentication) {
        gestionarSupervision.devolverAAtencion(id, actorId(authentication), request.motivo());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/transiciones/cerrar")
    @PreAuthorize("hasRole('SUPERVISOR')")
    @Operation(summary = "Cerrar solicitud (A3 valida 403 si el rol no es SUPERVISOR)")
    public ResponseEntity<Void> cerrar(@PathVariable("id") UUID id, @Valid @RequestBody MotivoRequest request,
                                        Authentication authentication) {
        gestionarSupervision.cerrar(id, actorId(authentication), request.motivo());
        return ResponseEntity.ok().build();
    }

    private static UUID actorId(Authentication authentication) {
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return UUID.fromString(jwt.getSubject());
        }
        throw new IllegalStateException("Principal no es un JWT válido");
    }
}
