package gov.iti.jets.NutriScan.ai.json_schema;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class SearchModelResponseJsonSchema {

    private SearchModelResponseJsonSchema() {
    }

    public static JsonNode create(ObjectMapper objectMapper) {
        String schema = """
            {
              "type": "object",
              "properties": {
                "ingredients": {
                  "type": "array",
                  "items": {
                    "type": "string"
                  }
                },
                "nutritionFacts": {
                  "type": "object",
                  "properties": {
                    "calories": {
                      "type": "integer"
                    },
                    "proteinGrams": {
                      "type": "number"
                    },
                    "carbsGrams": {
                      "type": "number"
                    },
                    "fatG": {
                      "type": "number"
                    },
                    "fiberGrams": {
                      "type": "number"
                    },
                    "sugarG": {
                      "type": "number"
                    },
                    "sodiumMg": {
                      "type": "number"
                    }
                  },
                  "required": [
                    "calories",
                    "proteinGrams",
                    "carbsGrams",
                    "fatG",
                    "fiberGrams",
                    "sugarG",
                    "sodiumMg"
                  ],
                  "additionalProperties": false
                }
              },
              "required": [
                "ingredients",
                "nutritionFacts"
              ],
              "additionalProperties": false
            }
            """;

        try {
            return objectMapper.readTree(schema);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Invalid search model JSON schema", exception);
        }
    }
}