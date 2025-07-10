//
//  ObjectModel.swift
//  BoxVistaIOS
//
//  Created by pol linger on 13/6/25.
//

import Foundation

struct ObjectDTO: Codable {
    let id: Int64
    let nombre: String
    let state: Bool
    let boxId: Int64
}

extension ObjectDTO {
    /// Convierte el DTO en el modelo de dominio `ObjectItem`
    func toObjectItem() -> ObjectItem {
        ObjectItem(
            id: id,
            nombre: nombre,
            state: state,
            boxId: boxId
        )
    }
}

struct ObjectItem: Identifiable, Hashable {
    let id: Int64
    let nombre: String
    var state: Bool
    let boxId: Int64
}
