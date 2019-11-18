package br.com.caelum.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import br.com.caelum.model.Loja;
import br.com.caelum.model.Produto;

@Repository
public class ProdutoDao {

	@PersistenceContext
	private EntityManager em;

	public List<Produto> getProdutos() {
		return em.createQuery("from Produto", Produto.class).getResultList();
	}

	public Produto getProduto(Integer id) {
		Produto produto = em.find(Produto.class, id);
		return produto;
	}

	/**
	 * @author lhsousa CriteriaBuilder utilizado para cria��o/Constru��o das querys,
	 *         possui m�todos de opera��o de buscas, "selects". Create Query � o
	 *         retorno dessa consulta, tenho que colocar no par�metro a classe que �
	 *         minha tabela.
	 * @param nome
	 * @param categoriaId
	 * @param lojaId
	 * @return
	 * @Transaction h� um EntityManager ativo
	 */
	public List<Produto> getProdutos(String nome, Integer categoriaId, Integer lojaId) {
		//Session � um forma de criar uma EntityManager no hibernate2
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		//CriteriaQuery no hibernate2 � equivalente a Criteria
		CriteriaQuery<Produto> query = criteriaBuilder.createQuery(Produto.class);
		Root<Produto> root = query.from(Produto.class);

		Path<String> nomePath = root.<String>get("nome");
		Path<Integer> lojaPath = root.<Loja>get("loja").<Integer>get("id");
		//SetFetchMode � equivalente a um Join que � criado dentro do meu if da para fazer direto
		//get sem ponto � do tipo gen�rico get com ponto � o get comum em ambas funciona
		Path<Integer> categoriaPath = root.join("categorias").<Integer>get("id");

		List<Predicate> predicates = new ArrayList<>();

		if (!nome.isEmpty()) {
			//Restrictions � a forma de fazer um Criteria builder no Hibernate2 filtros de busca
			Predicate nomeIgual = criteriaBuilder.like(nomePath, nome);
			predicates.add(nomeIgual);
		}
		if (categoriaId != null) {
			Predicate categoriaIgual = criteriaBuilder.equal(categoriaPath, categoriaId);
			predicates.add(categoriaIgual);
		}
		if (lojaId != null) {
			Predicate lojaIgual = criteriaBuilder.equal(lojaPath, lojaId);
			predicates.add(lojaIgual);
		}

		query.where((Predicate[]) predicates.toArray(new Predicate[0]));

		TypedQuery<Produto> typedQuery = em.createQuery(query);
		return typedQuery.getResultList();

	}

	public void insere(Produto produto) {
		if (produto.getId() == null)
			em.persist(produto);
		else
			em.merge(produto);
	}

	public List<Produto> getConsultaSimples() {

		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<Produto> query = criteriaBuilder.createQuery(Produto.class);

		// Ele faz a fun��o de um From
		Root<Produto> root = query.from(Produto.class);

		TypedQuery<Produto> typedQuery = em.createQuery(query);
		return typedQuery.getResultList();

	}

	public List<Produto> getProdutosTeste(String nome, Integer categoriaId, Integer lojaId) {
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<Produto> query = criteriaBuilder.createQuery(Produto.class);
		// Ele faz a mesma fun��o de um from
		Root<Produto> root = query.from(Produto.class);

		// Para garantir o retorno e criar mais de uma condi��o o nosso c�digo.
		// Para cada tipo de busca de um determinado campo, devo criar um path.
		// E minha List � de acordo com meu tipo para cada vari�vel.
		Path<String> nomePath = root.<String>get("nome");
		Path<Integer> categoriaIdPath = root.<Loja>get("Loja").<Integer>get("id");
		// Fazendo join a partir do produto
		Path<Integer> lojaIdPath = root.join("categorias").<Integer>get("id");

		// Guardando os predicates, para criar conectivo and, or, conjuction e disjuction e passar predicates para
		// cl�usula Where
		List<Predicate> predicates = new ArrayList<>();

		if (!nome.isEmpty()) {
			// Retornar um Predicate que � um m�todo de igualdade junto ao equal
			Predicate nomeIgual = criteriaBuilder.like(nomePath, "%" + nome + "%");
			predicates.add(nomeIgual);
		}
		if (categoriaId != null) {
			Predicate categoriaIgual = criteriaBuilder.equal(categoriaIdPath, categoriaId);
			predicates.add(categoriaIgual);
		}
		if (lojaId != null) {
			Predicate lojaIgual = criteriaBuilder.equal(lojaIdPath, lojaId);
			predicates.add(lojaIgual);
		}

		// Convertendo a lista em um array, passando para m�todo where.
		query.where((Predicate[]) predicates.toArray(new Predicate[0]));

		TypedQuery<Produto> typedQuery = em.createQuery(query);
		// Resultado da minha consulta.
		return typedQuery.getResultList();
	}

}
