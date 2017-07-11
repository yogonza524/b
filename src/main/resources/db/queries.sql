SELECT SUM(
                	(
                		CAST(h.paquete -> 'datos' ->> 'apostado' AS real) + 
                		CAST(h.paquete -> 'datos' ->> 'apostadoEnCicloDeBolasExtra' as real) - 
                		CAST(h.paquete -> 'datos' ->> 'ganado' AS real)
                	) * CAST(h.paquete -> 'datos' ->> 'denominacion' AS real)
                )
                as recaudado FROM historial_b1 h, configuracion c WHERE h.metodo = 'JUGAR' AND h.accion = 'RESPUESTA' AND c.ultimo_encendido < h.fecha

                
SELECT CAST(h.paquete -> 'datos' ->> 'apostado' AS real) as apostado,  
	CAST(h.paquete -> 'datos' ->> 'apostadoEnCicloDeBolasExtra' as real) as apostadoEnCicloDeBolasExtra,
	CAST(h.paquete -> 'datos' ->> 'ganado' AS real) as ganado
	
	FROM historial_b1 h, configuracion c 
	WHERE h.metodo = 'JUGAR' AND c.ultimo_encendido < h.fecha AND h.accion = 'RESPUESTA'


SELECT 
	h.paquete -> 'datos' ->> 'creditos', 
	h.paquete -> 'datos' ->> 'ganado',
	h.paquete -> 'datos' ->> 'apostado',
	h.paquete -> 'datos' ->> 'apostadoEnCicloDeBolasExtra',
	h.paquete -> 'datos' ->> 'apostadoEnBolasExtra', 
	h.paquete -> 'datos' ->> 'huboBonus' 
	
FROM historial_b1 h WHERE h.metodo = 'JUGAR' AND h.accion = 'RESPUESTA' ORDER BY fecha DESC LIMIT 1

SELECT * FROM historial_b1 ORDER BY fecha DESC LIMIT 1
