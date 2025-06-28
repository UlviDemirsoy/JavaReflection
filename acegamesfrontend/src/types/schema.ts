export type FieldDefinition = {
  type: string; // "String" | "Number" | "Boolean" | "Enum" | "Date" | "Object" | "Array"
  enumName?: string;
  fields?: Record<string, FieldDefinition>;
  items?: FieldDefinition;
  reference?: string;
};

export type ModelSchemaDto = {
  collection: string;
  displayName?: string;
  fields: Record<string, FieldDefinition>;
};
