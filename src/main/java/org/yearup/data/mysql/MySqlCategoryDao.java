package org.yearup.data.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao {
    private final DataSource dataSource;

    @Autowired
    public MySqlCategoryDao(DataSource dataSource) {
        super(dataSource);
        this.dataSource = dataSource;
    }

    @Override
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery(sql)) {


            while (resultSet.next()) {
                int categoryID = resultSet.getInt("category_id");
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                Category category = new Category(categoryID, name, description);
                categories.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // get all categories
        return categories;
    }


    @Override
    public Category getById(int categoryId) {
        String sql = "SELECT * FROM categories WHERE category_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, categoryId);
            try (
                    ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {

                    Category category = mapRow(resultSet);
                    return category;
                    // get category by id
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Category create(Category category) {
        Category newCategory = new Category();

        newCategory.setCategoryId(category.getCategoryId());
        newCategory.setName(category.getName());
        newCategory.setDescription(category.getDescription());
        // create a new category
        return newCategory;
    }

        @Override
        public void update ( int categoryId, Category category){
            String sql = "UPDATE categories SET name = ?, description = ? WHERE category_id = ?";

            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setString(1, category.getName());
                statement.setString(2,category.getDescription());
                statement.setInt(3, categoryId);

                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // update category
        }

        @Override
        public void delete ( int categoryId) {
            String sql = "DELETE FROM categories WHERE category_id = ?";

            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setInt(1, categoryId);

                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                // delete category
            }
        }

        @Override
        public Category insert (Category category){
            String sql = "INSERT INTO categories (name) VALUES (?)";

            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                statement.setString(1, category.getName());

                int affectedRows = statement.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Creating category failed, no rows affected.");
                }

                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        category.setCategoryId(generatedId);
                    } else {
                        throw new SQLException("Creating category failed, no ID obtained.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return category;
        }




    private Category mapRow (ResultSet row) throws SQLException {
            int categoryId = row.getInt("category_id");
            String name = row.getString("name");
            String description = row.getString("description");

            Category category = new Category() {{
                setCategoryId(categoryId);
                setName(name);
                setDescription(description);
            }};

            return category;
        }
    }



