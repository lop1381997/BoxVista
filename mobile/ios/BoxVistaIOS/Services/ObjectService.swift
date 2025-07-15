// ObjectService.swift
//
//  ObjectService.swift
//  BoxVistaIOS
//
//  Created by Pol Linger on 13/06/25.
//

import Foundation

/// Payload para actualizar un objeto
private struct UpdatePayload: Codable {
    let nombre: String
    let state: Bool
}

/// Protocolo para servicios de objetos
protocol ObjectServiceProtocol {
    /// URL base de la API (ej: http://localhost:3000/api)
    var baseURL: URL { get }
}

extension ObjectServiceProtocol {
    /// Obtiene todos los objetos asociados a una caja
    /// - Parameter boxId: Identificador de la caja
    /// - Throws: Error de red o decodificación
    /// - Returns: Array de `ObjectItem`
    func getObjects(for boxId: Int64) throws -> [ObjectItem] {
        let url = baseURL
            .appendingPathComponent("boxes")
            .appendingPathComponent("\(boxId)")
            .appendingPathComponent("objects")
        let data = try Data(contentsOf: url)
        let decoder = JSONDecoder()
        decoder.keyDecodingStrategy = .convertFromSnakeCase
        let dtos = try decoder.decode([ObjectDTO].self, from: data)
        return dtos.map { $0.toObjectItem() }
    }

    /// Obtiene un objeto concreto dentro de una caja
    /// - Parameters:
    ///   - boxId: Identificador de la caja
    ///   - objectId: Identificador del objeto
    /// - Throws: Error de red o decodificación
    /// - Returns: `ObjectItem`
    func getObject(boxId: Int64, objectId: Int64) throws -> ObjectItem {
        let url = baseURL
            .appendingPathComponent("boxes")
            .appendingPathComponent("\(boxId)")
            .appendingPathComponent("objects")
            .appendingPathComponent("\(objectId)")
        let data = try Data(contentsOf: url)
        let decoder = JSONDecoder()
        decoder.keyDecodingStrategy = .convertFromSnakeCase
        let dto = try decoder.decode(ObjectDTO.self, from: data)
        return dto.toObjectItem()
    }

    /// Actualiza el estado de un objeto en la caja
    /// - Parameters:
    ///   - object: Objeto a actualizar
    ///   - boxId: Identificador de la caja
    ///   - completion: Closure que recibe el resultado de la operación
    func updateObject(_ object: ObjectItem, boxId: Int64, completion: @escaping (Result<ObjectItem, Error>) -> Void) {
        let url = baseURL
            .appendingPathComponent("boxes")
            .appendingPathComponent("\(boxId)")
            .appendingPathComponent("objects")
            .appendingPathComponent("\(object.id)")
        
        var request = URLRequest(url: url)
        request.httpMethod = "PUT"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        // Create the update payload with proper types
        let updateData = UpdatePayload(nombre: object.nombre, state: object.state)
        
        do {
            let encoder = JSONEncoder()
            encoder.keyEncodingStrategy = .convertToSnakeCase
            request.httpBody = try encoder.encode(updateData)
        } catch {
            completion(.failure(error))
            return
        }
        
        // Perform asynchronous request
        URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                completion(.failure(error))
                return
            }
            
            // Check for HTTP errors
            if let httpResponse = response as? HTTPURLResponse {
                guard httpResponse.statusCode == 200 else {
                    let error = NSError(domain: "ObjectService", code: httpResponse.statusCode, userInfo: [
                        NSLocalizedDescriptionKey: "Failed to update object. Status code: \(httpResponse.statusCode)"
                    ])
                    completion(.failure(error))
                    return
                }
            }
            
            guard let data = data else {
                let error = NSError(domain: "ObjectService", code: 0, userInfo: [
                    NSLocalizedDescriptionKey: "No data received"
                ])
                completion(.failure(error))
                return
            }
            
            // Decode the response
            do {
                let decoder = JSONDecoder()
                decoder.keyDecodingStrategy = .convertFromSnakeCase
                let dto = try decoder.decode(ObjectDTO.self, from: data)
                completion(.success(dto.toObjectItem()))
            } catch {
                completion(.failure(error))
            }
        }.resume()
    }
}

/// Servicio real que apunta a tu API RESTful
struct ObjectService: ObjectServiceProtocol {
    let baseURL = URL(string: "http://localhost:3000/api")!
}

/// Servicio de prueba que carga JSON local de test
struct ObjectServiceTest: ObjectServiceProtocol {
    let baseURL: URL = {
        Bundle.main.url(forResource: "ObjectsTest", withExtension: "json")!
    }()
}
