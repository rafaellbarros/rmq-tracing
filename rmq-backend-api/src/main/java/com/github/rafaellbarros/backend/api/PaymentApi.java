package com.github.rafaellbarros.backend.api;

import com.github.rafaellbarros.backend.dto.PaymentDTO;
import com.github.rafaellbarros.backend.facade.PaymentFacede;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Tag(name = "Pagamentos", description = "Operações relacionadas a processamento de pagamentos")
public class PaymentApi {

    private final PaymentFacede paymentFacede;

    @PostMapping
    @Operation(
            summary = "Processar pagamento",
            description = "Endpoint para integração e processamento de pagamentos",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados do pagamento",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = PaymentDTO.class)
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Pagamento processado com sucesso",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Requisição inválida",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno no processamento do pagamento",
                    content = @Content
            )
    })
    public ResponseEntity<String> process(@RequestBody PaymentDTO paymentDTO) {
        return ResponseEntity.ok(paymentFacede.requestPayment(paymentDTO));
    }

}
