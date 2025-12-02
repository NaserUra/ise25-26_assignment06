package de.seuhd.campuscoffee.api.controller;

import de.seuhd.campuscoffee.api.dtos.PosDto;
import de.seuhd.campuscoffee.api.exceptions.ErrorResponse;
import de.seuhd.campuscoffee.api.mapper.PosDtoMapper;
import de.seuhd.campuscoffee.domain.model.CampusType;
import de.seuhd.campuscoffee.domain.ports.PosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static de.seuhd.campuscoffee.api.util.ControllerUtils.getLocation;

/**
 * Controller for handling POS-related API requests.
 */
@Tag(name = "Points of Sale (POS)", description = "Operations for managing coffee points of sale.")
@Controller
@RequestMapping("/api/pos")
@Slf4j
@RequiredArgsConstructor
public class PosController {
    private final PosService posService;
    private final PosDtoMapper posDtoMapper;

    @Operation(
            summary = "Get all POS.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = PosDto.class)
                            ),
                            description = "All POS as a JSON array."
                    )
            }
    )
    @GetMapping("")
    public ResponseEntity<List<PosDto>> getAll() {

        return ResponseEntity.ok(
                posService.getAll().stream()
                        .map(posDtoMapper::fromDomain)
                        .toList()
        );
    }

    @Operation(
            summary = "Get POS by ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PosDto.class)
                            ),
                            description = "The POS with the provided ID as a JSON object."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            ),
                            description = "No POS with the provided ID could be found."
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<PosDto> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                posDtoMapper.fromDomain(posService.getById(id))
        );
    }

    @Operation(
            summary = "Create a new POS.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PosDto.class)
                            ),
                            description = "The new POS as a JSON object."
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            ),
                            description = "Validation failed (e.g., invalid address, postal code, or bean validation errors)."
                    )
            }
    )
    @PostMapping("")
    public ResponseEntity<PosDto> create(
            @RequestBody @Valid PosDto posDto) {

        PosDto created = upsert(posDto);
        return ResponseEntity
                .created(getLocation(created.id()))
                .body(created);
    }

    @Operation(
            summary = "Get POS by name.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PosDto.class)
                            ),
                            description = "The POS with the provided name as a JSON object."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            ),
                            description = "No POS with the provided name could be found."
                    )
            }
    )
    @GetMapping("/filter")
    public ResponseEntity<PosDto> filter(
            @RequestParam("name") String name) {

        return ResponseEntity.ok(
                posDtoMapper.fromDomain(posService.getByName(name))
        );
    }

    @Operation(
            summary = "Import a new POS from an OpenStreetMap node.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PosDto.class)
                            ),
                            description = "The new POS imported from OSM as a JSON object."
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            ),
                            description = "Validation failed or the OSM node data is invalid."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            ),
                            description = "The OSM node with the provided ID could not be found."
                    )
            }
    )
    @PostMapping("/import/osm/{nodeId}")
    public ResponseEntity<PosDto> create(
            @PathVariable Long nodeId,
            @RequestBody CampusType campusType) {

        PosDto createdPos = posDtoMapper.fromDomain(
                posService.importFromOsmNode(nodeId, campusType)
        );
        return ResponseEntity
                .created(getLocation(createdPos.id()))
                .body(createdPos);
    }

    @Operation(
            summary = "Update an existing POS by ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PosDto.class)
                            ),
                            description = "The updated POS as a JSON object."
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            ),
                            description = "Validation failed: IDs in path and body do not match, invalid address, postal code, or bean validation errors."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            ),
                            description = "No POS with the provided ID could be found."
                    )
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<PosDto> update(
            @PathVariable Long id,
            @RequestBody @Valid PosDto posDto) {

        if (!id.equals(posDto.id())) {
            throw new IllegalArgumentException("POS ID in path and body do not match.");
        }
        return ResponseEntity.ok(
                upsert(posDto)
        );
    }

    @Operation(
            summary = "Delete a POS by ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "The POS was successfully deleted."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            ),
                            description = "No POS with the provided ID could be found."
                    )
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {
        posService.delete(id); // throws NotFoundException if no POS with the provided ID exists
        return ResponseEntity.noContent().build();
    }

    /**
     * Common upsert logic for create and update.
     *
     * @param posDto the POS DTO to map and upsert
     * @return the upserted POS mapped back to the DTO format.
     */
    private PosDto upsert(PosDto posDto) {
        return posDtoMapper.fromDomain(
                posService.upsert(
                        posDtoMapper.toDomain(posDto)
                )
        );
    }
}
