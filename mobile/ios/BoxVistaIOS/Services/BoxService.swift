// BoxService.swift
//
//  BoxService.swift
//  BoxVistaIOS
//
//  Created by Pol Linger on 09/06/25.
//

import Foundation
import Combine

/// Protocolo que define la URL base para Boxes
protocol BoxServiceProtocol {
    /// Base URL de la API (ej: http://localhost:3000/api)
    var baseURL: URL { get }
}

extension BoxServiceProtocol {
    /// Obtiene todas las cajas desde la API
    /// - Throws: Error de red o decodificación
    /// - Returns: Array de modelos de dominio `Box`
    func getBoxes() async throws -> [Box] {
        try await withCheckedThrowingContinuation { continuation in
            NetworkManager.shared.fetchBoxes { result in
                switch result {
                case .success(let boxes):
                    continuation.resume(returning: boxes)
                case .failure(let error):
                    continuation.resume(throwing: error)
                }
            }
        }
    }

    /// Obtiene una caja concreta por ID
    /// - Parameter id: Identificador de la caja
    /// - Throws: Error de red o decodificación
    /// - Returns: Modelo de dominio `Box`
    func getBox(id: Int64) async throws -> Box {
        try await withCheckedThrowingContinuation { continuation in
            NetworkManager.shared.fetchBox(id: Int64(id)) { result in
                switch result {
                case .success(let box):
                    continuation.resume(returning: box)
                case .failure(let error):
                    continuation.resume(throwing: error)
                }
            }
        }
    }

    /// Crea una nueva caja con sus objetos
    /// - Parameters:
    ///   - name: Nombre de la caja
    ///   - description: Descripción de la caja
    ///   - objects: Array de objetos que contendrá la caja
    /// - Returns: La caja creada
    func createBox(name: String, description: String, objects: [ObjectItem]) async throws -> Box {
        try await withCheckedThrowingContinuation { continuation in
            NetworkManager.shared.createBox(name: name, description: description, objects: objects) { result in
                switch result {
                case .success(let box):
                    continuation.resume(returning: box)
                case .failure(let error):
                    continuation.resume(throwing: error)
                }
            }
        }
    }
    
    func deleteBox(box: Box) async throws {
        try await withCheckedThrowingContinuation { continuation in
            NetworkManager.shared.deleteBox(id: box.id) { result in
                switch result {
                case .success:
                    continuation.resume()
                case .failure(let error):
                    continuation.resume(throwing: error)
                }
            }
        }
    }
        
            
    
}

/// Servicio real que apunta a tu API Express/SQLite
struct BoxService: BoxServiceProtocol {
    let baseURL = URL(string: "http://localhost:3000/api")!
}

/// Servicio de prueba que lee JSON local
struct BoxServiceTest: BoxServiceProtocol {
    let baseURL: URL = {
        Bundle.main.url(forResource: "BoxesTest", withExtension: "json")!
    }()
}
