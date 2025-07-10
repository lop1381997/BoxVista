//
//  ObjectVM.swift
//  BoxVistaIOS
//
//  Created by pol linger on 13/6/25.
//


import SwiftUI

/// View‑model responsable de exponer los objetos (`Item`) que pertenecen a una caja.
/// 1. Inyecta un `ObjectServiceProtocol` para facilitar tests.
/// 2. Publica los objetos mediante `@Published`.
/// 3. Proporciona un método `load(boxId:)` para rellenar el array cuando la vista lo necesite.
@MainActor
final class ObjectVM: ObservableObject {

    // MARK: - Dependencies
    private let service: ObjectServiceProtocol
    private let boxService: BoxServiceProtocol = BoxService()

    // MARK: - Published state
    @Published private(set) var objects: [ObjectItem] = []

    // MARK: - Initialiser
    init(service: ObjectServiceProtocol = ObjectService()) {
        self.service = service
    }

    // MARK: - Public API
    /// Carga los objetos que contiene la caja especificada.
    /// - Parameter boxId: Identificador de la caja.
    func load(boxId: Int64) {
        do { try objects = service.getObjects(for: boxId)}
        catch {
            print("Error al cargar los objetos: \(error)")
        }
    }
    
    

    func object(id: Int64) -> ObjectItem? {
            objects.first { $0.id == id }
    }
    
    func updateObjectState(id: Int64, to newState: Bool) {
        guard let index = objects.firstIndex(where: { $0.id == id }) else { return }
        objects[index].state = newState
    }
  
}
