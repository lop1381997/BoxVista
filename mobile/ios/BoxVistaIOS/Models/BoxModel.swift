//
//  Box.swift
//  BoxVistaIOS
//
//  Created by pol linger on 6/6/25.
//

import Foundation

struct BoxDTO: Codable {
    let id: Int64
    let name: String
    let description: String
    let objetos: [ObjectDTO]
}

extension BoxDTO {
    /// Convierte el DTO en el modelo de dominio `Box`
    func toBox() -> Box {
        Box(
            id: id,
            name: name,
            description: description,
            objects: objetos.map { $0.toObjectItem() }
        )
    }
}
struct Box: Identifiable, Hashable {
    let id: Int64
    let name: String
    let description: String
    var objects: [ObjectItem]
}
