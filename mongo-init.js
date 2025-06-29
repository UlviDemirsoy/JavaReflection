// MongoDB initialization script
db = db.getSiblingDB('acegames');

// Create collections if they don't exist
db.createCollection('modelSchemas');
db.createCollection('cascade');
db.createCollection('offer');
db.createCollection('purchaseproduct');
db.createCollection('skin');

// Create indexes for better performance
db.modelSchemas.createIndex({ "collection": 1 }, { unique: true });

print('MongoDB initialization completed for acegames database'); 