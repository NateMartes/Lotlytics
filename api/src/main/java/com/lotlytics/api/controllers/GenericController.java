package com.lotlytics.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.lotlytics.api.entites.ErrorMessage;
import com.lotlytics.api.entites.exceptions.*;
import lombok.extern.slf4j.Slf4j;

/*
 * The GenericController is useful for controllers that has to handle many errors
 * such as 404, 409, 403 etc.
 */
@Slf4j
public abstract class GenericController {

    /*
    * The CheckedSupplier interface allows functions to be called wihout checking for execptions.
    */
    public interface CheckedSupplier<T> {
        T get() throws Exception;
    }

    /**
     * The callServiceMethod method is used to handle many errors that could be thrown by
     * an API request, such as 404, 400, 403 etc.
     * 
     * @param <T> The type from the service method call.
     * @param serviceCall The service method to call and handle errors for.
     * @param responseCode The response code to return if the service call is successful (200, 204, 201  etc.)
     * @return A ResponseEntity of type T.
     */
    public static <T> ResponseEntity<?> callServiceMethod(CheckedSupplier<T> serviceCall, HttpStatus responseCode) {
        try {
            T result = serviceCall.get();
            return new ResponseEntity<T>(
                result,
                responseCode
            );
        } catch (BadRequestException e) {
            return new ResponseEntity<ErrorMessage>(
                new ErrorMessage(e.getMessage()),
                HttpStatus.BAD_REQUEST
            );
        } catch (NotFoundException e) {
            return new ResponseEntity<ErrorMessage>(
                new ErrorMessage(e.getMessage()),
                HttpStatus.NOT_FOUND
            );
        } catch (ForbiddenException e) {
            return new ResponseEntity<ErrorMessage>(
                new ErrorMessage(e.getMessage()),
                HttpStatus.FORBIDDEN
            );
        } catch (ConflictException e) {
            return new ResponseEntity<ErrorMessage>(
                new ErrorMessage(e.getMessage()),
                HttpStatus.CONFLICT
            );
        } catch (Exception e) {
            log.error("Unhandled Exception::" + e.getClass().getSimpleName() + " : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Throwable t) {
            System.out.println(t.toString());
            log.error("Unhandled Throwable::" + t.getClass().getSimpleName() + " : " + t.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * The callVoidServiceMethod method is used to handle many errors that could be thrown by
     * an API request, such as 404, 400, 403 etc.
     * 
     * This method should only be called if the service method returns void.
     * 
     * @param serviceCall The service method to call and handle errors for.
     * @param responseCode The response code to return if the service call is successful (200, 204, 201  etc.)
     * @return A ResponseEntity of type null.
     */
    public static ResponseEntity<?> callVoidServiceMethod(Runnable serviceCall, HttpStatus responseCode) {
        return callServiceMethod(() -> {
                serviceCall.run();
                return null;
            }, responseCode);
    }
}
