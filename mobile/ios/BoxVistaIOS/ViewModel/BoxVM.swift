import Foundation

@MainActor
class BoxVM: ObservableObject {
    @Published var boxes: [Box] = []
    @Published var isLoading = false
    @Published var errorMessage: String?
    private let service: BoxServiceProtocol

    init(service: BoxServiceProtocol = BoxService()) {
        self.service = service
    }

    func load() async {
        isLoading = true
        errorMessage = nil
        do {
            boxes = try await service.getBoxes()
        } catch {
            errorMessage = "Error cargando boxes: \(error.localizedDescription)"
        }
        isLoading = false
    }

//    func loadOne(id: Int64) async {
//        do {
//            let box = try await service.getBox(id: id)
//            // actualizar tu @Published correspondiente
//        } catch {
//            errorMessage = "Error cargando box \(id): \(error.localizedDescription)"
//        }
//    }
    
    /// Crea una nueva caja con sus objetos
    /// - Parameters:
    ///   - name: Nombre de la caja
    ///   - description: Descripción de la caja
    ///   - objects: Array de objetos que contendrá la caja
    /// - Returns: La caja creada o nil si hay error
    func createBox(name: String, description: String, objects: [ObjectItem]) async -> Box? {
        isLoading = true
        errorMessage = nil
        
        do {
            let newBox = try await service.createBox(name: name, description: description, objects: objects)
            // Add the new box to the local array
            boxes.append(newBox)
            isLoading = false
            return newBox
        } catch {
            errorMessage = getErrorMessage(from: error)
            isLoading = false
            return nil
        }
    }
    
    /// Convierte errores en mensajes amigables para el usuario
    private func getErrorMessage(from error: Error) -> String {
        if let apiError = error as? APIError {
            switch apiError {
            case .invalidURL:
                return "Error de configuración de la aplicación"
            case .invalidResponse:
                return "Error de comunicación con el servidor"
            case .decodingError:
                return "Error al procesar la respuesta del servidor"
            case .serverError(let code):
                return "Error del servidor (código \(code))"
            }
        }
        return "Error desconocido: \(error.localizedDescription)"
    }
    
    func deleteBox(_ box: Box) async {
        isLoading = true
        errorMessage = nil
        
        do {
            try await service.deleteBox(box: box)
            // Remove the box from the local array
            boxes.removeAll { $0.id == box.id }
        } catch {
            errorMessage = getErrorMessage(from: error)
        }
        
        isLoading = false
    }
        
}
