package py.edu.uc.jpasseratplp32025.mapper;

public interface IntegracionMapper<E, D> {
    D toDto(E external);
    E toEntity(D dto);
}