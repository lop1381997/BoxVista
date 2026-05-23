// ObjectService.swift
//
//  ObjectService.swift
//  BoxVistaIOS
//
//  Created by Pol Linger on 13/06/25.
//

import Foundation

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
    func getObjects(for boxId: Int64) async throws -> [ObjectItem] {
        try await withCheckedThrowingContinuation { continuation in
                NetworkManager.shared.fetchObjects(for: boxId) { result in
                    switch result {
                    case .success(let objects):
                        continuation.resume(returning: objects)
                    case .failure(let error):
                        continuation.resume(throwing: error)
                    }
                }
            }
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
        NetworkManager.shared.updateObject(object, in: boxId) { result in
            switch result {
            case .success(let object):
                completion(.success(object))
            case .failure(let error):
                completion(.failure(error))
            }
        }
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
