/*
 * ��ü���� ����� java������ ������ �繰�� Ŭ������ ����������, Database������ ������ �繰�� Entity��� ��ü �������� ǥ��
 * �ᱹ ��ü�� ǥ���ϴ� ����� �ٸ� �� ������ ����!	-> ���� �ݿ��̶�� ������ ����
 * 
 * ��ü���� ���� Ŭ������ �ν��Ͻ��� �����س��� ��Ǫ��(Ʋ)�̶��, database���� ���̺��� ���ڵ带 ������ �� �ִ� Ʋ�� ���� ����
 * �̶�, �ϳ��� ���ڵ�� �ᱹ �ϳ��� ��ü�� ���� �Ѵ�!
 * ���) ���̺� �����ϴ� ��ǰ ���ڵ� ���� �� 5�����, �����ڴ� �� ������ ���ڵ带 5���� �ν��Ͻ��� ���� ������ ��!!! 
 * 
 * �Ʒ��� Ŭ������ ���� �ۼ����� �ƴϰ�, �� ���� ���ڵ带 ��� ���� ���� ���� �뵵�θ� ����� Ŭ����!
 * ���ø����̼� ���� �о߿����� �̷��� ������ Ŭ������ ������ VO(Value Object), Ȥ�� DTO(Data Transfer Object)��� ��
 * Value Object				���� ��� ��ü
 * Data Transfer Object		���� �����ϱ� ���� ��ü
 * */
package book;

/*
 * ������ ���� Ŭ����, Dummy Ŭ����
 * ���� �����͸� ��� �����!
 * �迭���� ������)
 * �迭�� Data�� �ڷ����� ������ ����� �� ����
 * �迭�� ��ü�� ������ �ٸ� ������� ���ߵǹǷ�, ��ü�� ó���ϴ� ���� �ξ� �� �۾����,���� �� ����!
 * */
public class SubCategory {
	// ������ ����, �����͸��� ���� Ŭ�����̹Ƿ� private���� ������ ��ȣ -> ����ȭ
	private int subcategory_id;
	private String category_name;
	private int topcategory_id;
	
	public int getSubcategory_id() {
		return subcategory_id;
	}
	
	// private���� ���� �Ұ����ϹǷ� getter/setter ����
	public void setSubcategory_id(int subcategory_id) {
		this.subcategory_id = subcategory_id;
	}
	public String getCategory_name() {
		return category_name;
	}
	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}
	public int getTopcategory_id() {
		return topcategory_id;
	}
	public void setTopcategory_id(int topcategory_id) {
		this.topcategory_id = topcategory_id;
	}
	
	
}
