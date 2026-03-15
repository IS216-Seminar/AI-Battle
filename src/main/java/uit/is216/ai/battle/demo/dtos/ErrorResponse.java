package uit.is216.ai.battle.demo.dtos;

public record ErrorResponse(String message, String code) {
    public ErrorResponse(String message) {
        this(message, "ERROR");
    }
}
