package gov.iti.jets.NutriScan.ai.foodsafety;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class FoodSafetyJsonSchema {

    private FoodSafetyJsonSchema() {
    }

    public static JsonNode create(ObjectMapper objectMapper) {
        String schema = """
            {
              "type": "object",
              "properties": {
                "verdict": {
                  "type": "string",
                  "enum": [
                    "SAFE",
                    "UNSAFE",
                    "CAUTION"
                  ]
                },
                "flaggedIngredients": {
                  "type": "array",
                  "items": {
                    "type": "object",
                    "properties": {
                      "ingredient": {
                        "type": "string"
                      },
                      "reason": {
                        "type": "string"
                      },
                      "type": {
                        "type": "string",
                        "enum": [
                          "ALLERGY",
                          "CHRONIC_CONDITION"
                        ]
                      },
                      "name": {
                        "type": "array",
                        "items": {
                          "type": "string"
                        }
                      }
                    },
                    "required": [
                      "ingredient",
                      "reason",
                      "type",
                      "name"
                    ],
                    "additionalProperties": false
                  }
                },
                "summary": {
                  "type": "string"
                }
              },
              "required": [
                "verdict",
                "flaggedIngredients",
                "summary"
              ],
              "additionalProperties": false
            }
            """;

        try {
            return objectMapper.readTree(schema);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException(
                "Invalid food-safety JSON schema",
                exception
            );
        }
    }
}
