package com.moments.db.utils;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lte;

import java.util.List;

import org.bson.conversions.Bson;

/**
 * Utility methods for adding null safe bson queries
 */
public class BsonHelper {

	private List<Bson> conditions;

	public BsonHelper(List<Bson> conditions) {
		this.conditions = conditions;
	}

	public <Item> void addEqBson(String field, Item item) {
		if (item != null) {
			conditions.add(eq(field, item));
		}
	}

	public <Item> void addGteBson(String field, Item item) {
		if (item != null) {
			conditions.add(gte(field, item));
		}
	}
	
	public <Item> void addGtBson(String field, Item item) {
		if (item != null) {
			conditions.add(gt(field, item));
		}
	}

	public <Item> void addLteBson(String field, Item item) {
		if (item != null) {
			conditions.add(lte(field, item));
		}
	}
	
}
