import Foundation

@MainActor
class BoxDetailViewVM: ObservableObject {
    private let boxService: BoxServiceProtocol = BoxService()
    private let objectService: ObjectServiceProtocol = ObjectService()

    // Hacemos 'box' opcional para poder inicializar el VM sin tenerla cargada aún
    @Published var box: Box?
    @Published var isLoading: Bool = false
    @Published var errorMessage: String? = nil
    @Published private(set) var objects: [ObjectItem] = []

    /// Crea el VM y carga la caja + objetos por su ID.
    init() {
        isLoading = true
    }

    /// Carga la caja y sus objetos. Maneja estado y errores.
    func load(boxID: Int64) async {
        isLoading = true
        errorMessage = nil
        defer { isLoading = false }

        do {
            let fetchedBox = try await boxService.getBox(id: boxID)
            self.box = fetchedBox
            print("cargando caja \(fetchedBox.name) con id \(fetchedBox.id)")
            self.objects = try await objectService.getObjects(for: boxID)
            
            print("cargados \(objects.count) objetos")
        } catch {
            self.errorMessage = error.localizedDescription
        }
    }

    /// Borra la caja actual. Devuelve `true` si se borra correctamente.
    func deleteBox() async -> Bool {
        guard let currentBox = box else {
            self.errorMessage = "No hay caja cargada para borrar."
            return false
        }

        isLoading = true
        errorMessage = nil
        defer { isLoading = false }

        do {
            try await boxService.deleteBox(box: currentBox)
            return true
        } catch {
            self.errorMessage = error.localizedDescription
            return false
        }
    }
    
    func updateObjectState(id: Int64, to newState: Bool) {
            guard let index = objects.firstIndex(where: { $0.id == id }) else { return }
            objects[index].state = newState

            let updatedObject = objects[index]
            objectService.updateObject(updatedObject, boxId: updatedObject.boxId) { [weak self] result in
                DispatchQueue.main.async {
                    switch result {
                    case .success(let obj):
                        if let idx = self?.objects.firstIndex(where: { $0.id == obj.id }) {
                            self?.objects[idx] = obj
                        }
                    case .failure(let error):
                        print("Error updating object: \(error)")
                    }
                }
            }
        }
}
