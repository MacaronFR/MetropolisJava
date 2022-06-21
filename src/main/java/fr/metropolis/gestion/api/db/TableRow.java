package fr.metropolis.gestion.api.db;

import java.sql.ResultSet;

interface TableRow<T>{
	T buildFromResultSet(ResultSet data);
}