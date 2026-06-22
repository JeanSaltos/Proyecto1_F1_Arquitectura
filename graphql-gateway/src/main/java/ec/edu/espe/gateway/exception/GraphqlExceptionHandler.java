package ec.edu.espe.gateway.exception;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

import java.util.Map;

@Component
public class GraphqlExceptionHandler extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        if (ex instanceof RestClientResponseException restException) {
            return GraphqlErrorBuilder.newError(env)
                    .errorType(restException.getStatusCode().is4xxClientError()
                            ? ErrorType.BAD_REQUEST
                            : ErrorType.INTERNAL_ERROR)
                    .message("Error del servicio backend: " + restException.getStatusText())
                    .extensions(Map.of(
                            "classification", "BACKEND_HTTP_ERROR",
                            "status", restException.getStatusCode().value()))
                    .build();
        }

        if (ex instanceof ResourceAccessException) {
            return GraphqlErrorBuilder.newError(env)
                    .errorType(ErrorType.INTERNAL_ERROR)
                    .message("Servicio backend no disponible")
                    .extensions(Map.of("classification", "BACKEND_UNAVAILABLE"))
                    .build();
        }

        if (ex instanceof IllegalArgumentException) {
            return GraphqlErrorBuilder.newError(env)
                    .errorType(ErrorType.BAD_REQUEST)
                    .message(ex.getMessage())
                    .extensions(Map.of("classification", "BAD_REQUEST"))
                    .build();
        }

        return GraphqlErrorBuilder.newError(env)
                .errorType(ErrorType.INTERNAL_ERROR)
                .message("Error interno procesando la operacion GraphQL")
                .extensions(Map.of("classification", "INTERNAL_ERROR"))
                .build();
    }
}
