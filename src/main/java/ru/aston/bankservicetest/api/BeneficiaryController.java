package ru.aston.bankservicetest.api;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.aston.bankservicetest.exception.BeneficiaryNotFoundException;
import ru.aston.bankservicetest.model.dto.BeneficiaryDto;
import ru.aston.bankservicetest.service.BeneficiaryService;

import java.util.List;

@Tag(name = "Beneficiary", description = "Beneficiary API")
@RestController
@RequestMapping("/api/v1/beneficiary")
@RequiredArgsConstructor
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;

    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = BeneficiaryDto.class))}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    })
    @GetMapping
    public ResponseEntity<?> getAllBeneficiary() {
        final List<BeneficiaryDto> beneficiaryDtoList = beneficiaryService.getAll();

        if (CollectionUtils.isEmpty(beneficiaryDtoList))
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(beneficiaryDtoList);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = BeneficiaryDto.class))}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(implementation = String.class))})
    })
    @GetMapping("/{username}")
    public ResponseEntity<?> getBeneficiaryByUserName(@PathVariable String username) {
        try {
            return ResponseEntity.ok(beneficiaryService.findByUsername(username));
        } catch (BeneficiaryNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
