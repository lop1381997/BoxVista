import Foundation

class BoxVM: ObservableObject {
    @Published var boxes: [Box] = []
    private let service: BoxServiceProtocol

    init(service: BoxServiceProtocol = BoxService()) {
        self.service = service
    }

    func load() async {
        do {
            boxes = try await service.getBoxes()
        } catch {
            print("Error cargando boxes:", error)
        }
    }

    func loadOne(id: Int64) async {
        do {
            let box = try await service.getBox(id: id)
            // actualizar tu @Published correspondiente
        } catch {
            print("Error cargando box \(id):", error)
        }
    }
}
